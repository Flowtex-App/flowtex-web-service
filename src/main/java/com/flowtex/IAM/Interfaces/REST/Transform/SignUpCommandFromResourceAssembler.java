package com.flowtex.IAM.Interfaces.REST.Transform;

import com.flowtex.IAM.Domain.Model.Commands.SignUpCommand;
import com.flowtex.IAM.Domain.Model.ValueObjects.Roles;
import com.flowtex.IAM.Interfaces.REST.Resources.SignUpResource;

import java.util.List;
import java.util.Optional;

public class SignUpCommandFromResourceAssembler {

    public static SignUpCommand toCommand(SignUpResource resource) {
        List<Roles> roles = Optional.ofNullable(resource.roles())
                .map(list -> list.stream().map(Roles::valueOf).toList())
                .orElse(List.of(Roles.ROLE_USER));

        return new SignUpCommand(
                resource.username(),
                resource.email(),
                resource.fullName(),
                resource.password(),
                roles);
    }
}
