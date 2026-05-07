package com.flowtex.IAM.Interfaces.REST.Resources;

import java.util.List;

public record AuthenticatedUserResource(
        Long id,
        String username,
        String email,
        String fullName,
        List<String> roles,
        String token
) {
}
