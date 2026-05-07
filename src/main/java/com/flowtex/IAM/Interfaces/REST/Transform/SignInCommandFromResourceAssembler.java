package com.flowtex.IAM.Interfaces.REST.Transform;

import com.flowtex.IAM.Domain.Model.Commands.SignInCommand;
import com.flowtex.IAM.Interfaces.REST.Resources.SignInResource;

public class SignInCommandFromResourceAssembler {

    public static SignInCommand toCommand(SignInResource resource) {
        return new SignInCommand(resource.username(), resource.password());
    }
}
