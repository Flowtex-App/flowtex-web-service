package com.flowtex.Tracking.Interfaces.REST.Resources;

import java.time.LocalDateTime;

public record SubmissionAuditEventResource(
        Long id,
        String eventType,
        Long actorUserId,
        String actorLabel,
        String fieldKey,
        String fieldLabel,
        String oldValue,
        String newValue,
        String description,
        LocalDateTime timestamp
) {
}
