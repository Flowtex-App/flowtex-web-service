package com.flowtex.IAM.Interfaces.REST.Transform;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Interfaces.REST.Resources.AuthenticatedUserResource;
import com.flowtex.IAM.Interfaces.REST.Resources.UserResource;

public class AuthenticatedUserResourceFromEntityAssembler {

    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getEmployeeCode(),
                user.getPosition() != null ? user.getPosition().name() : null,
                user.getPosition() != null ? user.getPosition().getLabel() : null,
                user.getPositionSpecialty(),
                user.getArea() != null ? user.getArea().name() : null,
                user.getArea() != null ? user.getArea().getLabel() : null,
                user.getRoleNames(),
                token);
    }

    public static UserResource toUserResourceFromEntity(User user) {
        return new UserResource(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getEmployeeCode(),
                user.getPosition() != null ? user.getPosition().name() : null,
                user.getPosition() != null ? user.getPosition().getLabel() : null,
                user.getPositionSpecialty(),
                user.getArea() != null ? user.getArea().name() : null,
                user.getArea() != null ? user.getArea().getLabel() : null,
                user.getRoleNames());
    }
}
