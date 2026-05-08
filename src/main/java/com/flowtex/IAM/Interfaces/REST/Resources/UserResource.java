package com.flowtex.IAM.Interfaces.REST.Resources;

import java.util.List;

public record UserResource(
        Long id,
        String username,
        String email,
        String fullName,
        String employeeCode,
        String position,
        String positionLabel,
        String positionSpecialty,
        String area,
        String areaLabel,
        List<String> roles
) {
}
