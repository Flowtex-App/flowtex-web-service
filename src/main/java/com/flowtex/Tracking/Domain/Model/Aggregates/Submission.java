package com.flowtex.Tracking.Domain.Model.Aggregates;

import com.flowtex.Tracking.Domain.Model.Entities.SubmissionAuditEvent;
import com.flowtex.Tracking.Domain.Model.Entities.SubmissionStepExecution;
import com.flowtex.Tracking.Domain.Model.ValueObjects.SubmissionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_code", nullable = false, unique = true, length = 20)
    private String ticketCode;

    @Column(name = "form_id", nullable = false)
    private Long formId;

    @Column(name = "form_version", nullable = false)
    private int formVersion;

    @Column(name = "workflow_id")
    private Long workflowId;

    /** Snapshot serializado del formulario al momento del envío. */
    @Column(name = "form_snapshot", nullable = false, columnDefinition = "LONGTEXT")
    private String formSnapshot;

    /** Snapshot serializado del workflow al momento del envío. */
    @Column(name = "workflow_snapshot", columnDefinition = "LONGTEXT")
    private String workflowSnapshot;

    @Column(name = "submitter_id", nullable = false)
    private Long submitterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status = SubmissionStatus.IN_PROGRESS;

    /** JSON object con los valores ingresados por el solicitante. */
    @Column(name = "data_json", nullable = false, columnDefinition = "LONGTEXT")
    private String dataJson;

    @Column(name = "current_step_ref", length = 80)
    private String currentStepRef;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SubmissionStepExecution> stepExecutions = new ArrayList<>();

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SubmissionAuditEvent> auditEvents = new ArrayList<>();

    public Submission() {}

    public Submission(String ticketCode, Long formId, int formVersion, Long workflowId,
                      String formSnapshot, String workflowSnapshot,
                      Long submitterId, String dataJson) {
        this.ticketCode = ticketCode;
        this.formId = formId;
        this.formVersion = formVersion;
        this.workflowId = workflowId;
        this.formSnapshot = formSnapshot;
        this.workflowSnapshot = workflowSnapshot;
        this.submitterId = submitterId;
        this.dataJson = dataJson;
        this.status = workflowSnapshot == null ? SubmissionStatus.APPROVED : SubmissionStatus.IN_PROGRESS;
        this.submittedAt = LocalDateTime.now();
        if (this.status == SubmissionStatus.APPROVED) this.completedAt = this.submittedAt;
    }

    public void updateData(String dataJson) {
        this.dataJson = dataJson;
    }

    public void setCurrentStepRef(String ref) { this.currentStepRef = ref; }

    public void markApproved() {
        this.status = SubmissionStatus.APPROVED;
        this.completedAt = LocalDateTime.now();
        this.currentStepRef = null;
    }

    public void markRejected() {
        this.status = SubmissionStatus.REJECTED;
        this.completedAt = LocalDateTime.now();
        this.currentStepRef = null;
    }

    public void markReturned() {
        this.status = SubmissionStatus.RETURNED;
    }

    public void markInProgress() {
        this.status = SubmissionStatus.IN_PROGRESS;
        this.completedAt = null;
    }

    public void markCanceled() {
        this.status = SubmissionStatus.CANCELED;
        this.completedAt = LocalDateTime.now();
    }

    public void appendStepExecution(SubmissionStepExecution exec) {
        exec.attachToSubmission(this);
        this.stepExecutions.add(exec);
    }

    public void appendAudit(SubmissionAuditEvent event) {
        event.attachToSubmission(this);
        this.auditEvents.add(event);
    }

    public List<SubmissionStepExecution> getOrderedExecutions() {
        List<SubmissionStepExecution> copy = new ArrayList<>(stepExecutions);
        copy.sort(Comparator.comparing(SubmissionStepExecution::getQueuedAt)
                .thenComparingInt(SubmissionStepExecution::getPosition));
        return copy;
    }

    public List<SubmissionAuditEvent> getOrderedAudit() {
        List<SubmissionAuditEvent> copy = new ArrayList<>(auditEvents);
        copy.sort(Comparator.comparing(SubmissionAuditEvent::getTimestamp));
        return copy;
    }

    public Long getId() { return id; }
    public String getTicketCode() { return ticketCode; }
    public Long getFormId() { return formId; }
    public int getFormVersion() { return formVersion; }
    public Long getWorkflowId() { return workflowId; }
    public String getFormSnapshot() { return formSnapshot; }
    public String getWorkflowSnapshot() { return workflowSnapshot; }
    public Long getSubmitterId() { return submitterId; }
    public SubmissionStatus getStatus() { return status; }
    public String getDataJson() { return dataJson; }
    public String getCurrentStepRef() { return currentStepRef; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
