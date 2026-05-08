package com.flowtex.Tracking.Domain.Model.Entities;

import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.ValueObjects.AssignmentKind;
import com.flowtex.Tracking.Domain.Model.ValueObjects.Decision;
import com.flowtex.Tracking.Domain.Model.ValueObjects.StepExecutionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission_step_executions")
public class SubmissionStepExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    /** Reference to the step within the workflow snapshot (its tempId/id at snapshot time). */
    @Column(name = "step_ref", nullable = false, length = 80)
    private String stepRef;

    @Column(name = "step_label", nullable = false, length = 160)
    private String stepLabel;

    @Column(nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_kind", nullable = false, length = 20)
    private AssignmentKind assignmentKind;

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    @Column(name = "assigned_user_label", length = 160)
    private String assignedUserLabel;

    @Column(name = "assigned_area", length = 40)
    private String assignedArea;

    @Column(name = "assigned_position", length = 20)
    private String assignedPosition;

    @Column(name = "assigned_role", length = 80)
    private String assignedRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StepExecutionStatus status = StepExecutionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Decision decision;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "queued_at", nullable = false)
    private LocalDateTime queuedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "decided_by_user_id")
    private Long decidedByUserId;

    public SubmissionStepExecution() {}

    public SubmissionStepExecution(String stepRef, String stepLabel, int position,
                                   AssignmentKind kind,
                                   Long assignedUserId, String assignedUserLabel,
                                   String assignedArea, String assignedPosition, String assignedRole) {
        this.stepRef = stepRef;
        this.stepLabel = stepLabel;
        this.position = position;
        this.assignmentKind = kind;
        this.assignedUserId = assignedUserId;
        this.assignedUserLabel = assignedUserLabel;
        this.assignedArea = assignedArea;
        this.assignedPosition = assignedPosition;
        this.assignedRole = assignedRole;
        this.queuedAt = LocalDateTime.now();
    }

    public void attachToSubmission(Submission submission) { this.submission = submission; }

    public void recordDecision(Decision decision, String comments, Long deciderId) {
        this.decision = decision;
        this.comments = comments;
        this.decidedByUserId = deciderId;
        this.completedAt = LocalDateTime.now();
        if (this.startedAt == null) this.startedAt = this.completedAt;
        switch (decision) {
            case APPROVE -> this.status = StepExecutionStatus.APPROVED;
            case REJECT  -> this.status = StepExecutionStatus.REJECTED;
            case RETURN  -> this.status = StepExecutionStatus.RETURNED;
        }
    }

    public void markSkipped() {
        this.status = StepExecutionStatus.SKIPPED;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Submission getSubmission() { return submission; }
    public String getStepRef() { return stepRef; }
    public String getStepLabel() { return stepLabel; }
    public int getPosition() { return position; }
    public AssignmentKind getAssignmentKind() { return assignmentKind; }
    public Long getAssignedUserId() { return assignedUserId; }
    public String getAssignedUserLabel() { return assignedUserLabel; }
    public String getAssignedArea() { return assignedArea; }
    public String getAssignedPosition() { return assignedPosition; }
    public String getAssignedRole() { return assignedRole; }
    public StepExecutionStatus getStatus() { return status; }
    public Decision getDecision() { return decision; }
    public String getComments() { return comments; }
    public LocalDateTime getQueuedAt() { return queuedAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Long getDecidedByUserId() { return decidedByUserId; }
}
