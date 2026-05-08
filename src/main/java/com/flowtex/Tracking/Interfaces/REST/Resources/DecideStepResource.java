package com.flowtex.Tracking.Interfaces.REST.Resources;

import jakarta.validation.constraints.NotBlank;

public record DecideStepResource(
        @NotBlank String decision,
        String comments
) {
}
