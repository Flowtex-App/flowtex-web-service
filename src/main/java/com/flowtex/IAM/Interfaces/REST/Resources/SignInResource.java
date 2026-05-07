package com.flowtex.IAM.Interfaces.REST.Resources;

import jakarta.validation.constraints.NotBlank;

public record SignInResource(@NotBlank String username, @NotBlank String password) {
}
