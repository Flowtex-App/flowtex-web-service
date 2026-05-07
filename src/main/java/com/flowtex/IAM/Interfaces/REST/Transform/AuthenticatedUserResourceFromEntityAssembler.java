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
                user.getRoleNames(),
                token);
    }

    public static UserResource toUserResourceFromEntity(User user) {
        return new UserResource(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRoleNames());
    }
}
