package com.flowtex.Tracking.Interfaces.REST.Resources;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record CreateSubmissionResource(
        @NotNull Long formId,
        Map<String, Object> data
) {
}
