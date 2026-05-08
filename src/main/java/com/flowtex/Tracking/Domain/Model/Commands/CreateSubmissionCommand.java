package com.flowtex.Tracking.Domain.Model.Commands;

import java.util.Map;

public record CreateSubmissionCommand(
        Long formId,
        Long submitterId,
        Map<String, Object> data
) {
}
