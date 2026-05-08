package com.flowtex.Tracking.Interfaces.REST.Resources;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record UpdateSubmissionDataResource(
        @NotNull Map<String, Object> data
) {
}
