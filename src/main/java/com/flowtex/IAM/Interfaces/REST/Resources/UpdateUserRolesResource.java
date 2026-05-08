package com.flowtex.IAM.Interfaces.REST.Resources;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateUserRolesResource(
        @NotEmpty List<String> roles
) {
}
