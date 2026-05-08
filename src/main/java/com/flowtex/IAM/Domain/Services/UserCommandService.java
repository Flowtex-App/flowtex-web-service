package com.flowtex.IAM.Domain.Services;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Model.Commands.SignInCommand;
import com.flowtex.IAM.Domain.Model.Commands.SignUpCommand;
import com.flowtex.IAM.Domain.Model.Commands.UpdateUserRolesCommand;
import org.springframework.data.util.Pair;

import java.util.Optional;

public interface UserCommandService {
    Optional<User> handle(SignUpCommand command);
    Optional<Pair<User, String>> handle(SignInCommand command);
    Optional<User> handle(UpdateUserRolesCommand command);
}
