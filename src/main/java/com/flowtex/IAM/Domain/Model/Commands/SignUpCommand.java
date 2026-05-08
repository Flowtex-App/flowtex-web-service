package com.flowtex.IAM.Domain.Model.Commands;

import com.flowtex.IAM.Domain.Model.ValueObjects.Area;
import com.flowtex.IAM.Domain.Model.ValueObjects.Position;
import com.flowtex.IAM.Domain.Model.ValueObjects.Roles;

import java.util.List;

public record SignUpCommand(
        String username,
        String email,
        String fullName,
        String password,
        String employeeCode,
        Position position,
        String positionSpecialty,
        Area area,
        List<Roles> roles
) {
}
