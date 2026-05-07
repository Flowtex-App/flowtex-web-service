package com.flowtex.IAM.Interfaces.REST.Resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SignUpResource(
        @NotBlank @Size(min = 3, max = 80) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 2, max = 160) String fullName,
        @NotBlank @Size(min = 8, max = 100) String password,
        List<String> roles
) {
}
