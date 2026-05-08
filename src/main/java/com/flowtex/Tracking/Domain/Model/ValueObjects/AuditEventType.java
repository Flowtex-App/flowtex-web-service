package com.flowtex.Tracking.Domain.Model.ValueObjects;

public enum AuditEventType {
    CREATED,
    SUBMITTED,
    FIELD_CHANGED,
    COMMENTED,
    STEP_ASSIGNED,
    STEP_APPROVED,
    STEP_REJECTED,
    STEP_RETURNED,
    STEP_SKIPPED,
    WORKFLOW_COMPLETED,
    RESUBMITTED,
    CANCELED
}
