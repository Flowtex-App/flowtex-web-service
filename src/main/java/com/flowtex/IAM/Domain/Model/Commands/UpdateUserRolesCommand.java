package com.flowtex.IAM.Domain.Model.Commands;

import com.flowtex.IAM.Domain.Model.ValueObjects.Roles;

import java.util.List;

public record UpdateUserRolesCommand(
        Long userId,
        List<Roles> roles
) {
}
