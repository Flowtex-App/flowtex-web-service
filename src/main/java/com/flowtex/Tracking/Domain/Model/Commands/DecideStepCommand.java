package com.flowtex.Tracking.Domain.Model.Commands;

import com.flowtex.Tracking.Domain.Model.ValueObjects.Decision;

public record DecideStepCommand(
        Long submissionId,
        Long stepExecutionId,
        Long actorUserId,
        Decision decision,
        String comments
) {
}
