package com.flowtex.Tracking.Interfaces.REST.Resources;

import java.time.LocalDateTime;

public record SubmissionStepExecutionResource(
        Long id,
        String stepRef,
        String stepLabel,
        int position,
        String assignmentKind,
        Long assignedUserId,
        String assignedUserLabel,
        String assignedArea,
        String assignedPosition,
        String assignedRole,
        String status,
        String decision,
        String comments,
        LocalDateTime queuedAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        Long decidedByUserId
) {
}
