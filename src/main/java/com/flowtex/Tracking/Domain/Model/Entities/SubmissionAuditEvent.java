package com.flowtex.Tracking.Domain.Model.Entities;

import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.ValueObjects.AuditEventType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Inmutable audit event sobre una submission.
 *
 * No tiene setters: una vez creado, no se modifica. La inmutabilidad es la
 * garantía de auditoría que pide el negocio (RNF05).
 */
@Entity
@Table(name = "submission_audit_events")
public class SubmissionAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private AuditEventType eventType;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "actor_label", length = 160)
    private String actorLabel;

    @Column(name = "field_key", length = 80)
    private String fieldKey;

    @Column(name = "field_label", length = 160)
    private String fieldLabel;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(length = 500)
    private String description;

    @Column(name = "data_json", columnDefinition = "TEXT")
    private String dataJson;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public SubmissionAuditEvent() {}

    public SubmissionAuditEvent(AuditEventType type, Long actorUserId, String actorLabel,
                                String fieldKey, String fieldLabel,
                                String oldValue, String newValue,
                                String description, String dataJson) {
        this.eventType = type;
        this.actorUserId = actorUserId;
        this.actorLabel = actorLabel;
        this.fieldKey = fieldKey;
        this.fieldLabel = fieldLabel;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.description = description;
        this.dataJson = dataJson;
        this.timestamp = LocalDateTime.now();
    }

    public void attachToSubmission(Submission submission) { this.submission = submission; }

    public Long getId() { return id; }
    public Submission getSubmission() { return submission; }
    public AuditEventType getEventType() { return eventType; }
    public Long getActorUserId() { return actorUserId; }
    public String getActorLabel() { return actorLabel; }
    public String getFieldKey() { return fieldKey; }
    public String getFieldLabel() { return fieldLabel; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getDescription() { return description; }
    public String getDataJson() { return dataJson; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
