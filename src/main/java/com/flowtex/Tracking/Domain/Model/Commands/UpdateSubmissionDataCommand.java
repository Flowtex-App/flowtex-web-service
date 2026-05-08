package com.flowtex.Tracking.Domain.Model.Commands;

import java.util.Map;

public record UpdateSubmissionDataCommand(
        Long submissionId,
        Long actorUserId,
        Map<String, Object> data
) {
}
