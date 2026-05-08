package com.flowtex.IAM.Interfaces.REST.Resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SignUpResource(
        @NotBlank @Size(min = 3, max = 80) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 2, max = 160) String fullName,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Pattern(regexp = "^C\\d{5}$",
                message = "El código de empleado debe tener el formato C seguido de 5 dígitos (ej. C12345)")
        String employeeCode,
        @NotNull String position,
        @Size(max = 120) String positionSpecialty,
        @NotNull String area,
        List<String> roles
) {
}
