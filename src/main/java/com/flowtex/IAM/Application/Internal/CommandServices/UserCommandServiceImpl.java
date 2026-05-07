package com.flowtex.IAM.Application.Internal.CommandServices;

import com.flowtex.IAM.Application.Internal.OutboundServices.HashingService;
import com.flowtex.IAM.Application.Internal.OutboundServices.TokenService;
import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Model.Commands.SignInCommand;
import com.flowtex.IAM.Domain.Model.Commands.SignUpCommand;
import com.flowtex.IAM.Domain.Model.Entities.Role;
import com.flowtex.IAM.Domain.Model.ValueObjects.Roles;
import com.flowtex.IAM.Domain.Repositories.RoleRepository;
import com.flowtex.IAM.Domain.Repositories.UserRepository;
import com.flowtex.IAM.Domain.Services.UserCommandService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public UserCommandServiceImpl(UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  HashingService hashingService,
                                  TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new IllegalArgumentException("Username already exists: " + command.username());
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Email already in use: " + command.email());
        }

        List<Roles> requestedRoles = command.roles() == null || command.roles().isEmpty()
                ? List.of(Roles.ROLE_USER)
                : command.roles();

        List<Role> roleEntities = requestedRoles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(new Role(roleName))))
                .toList();

        User user = new User(
                command.username(),
                command.email(),
                command.fullName(),
                hashingService.encode(command.password()));
        user.addRoles(roleEntities);

        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<Pair<User, String>> handle(SignInCommand command) {
        Optional<User> user = userRepository.findByUsername(command.username());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        if (!hashingService.matches(command.password(), user.get().getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = tokenService.generateToken(user.get().getUsername());
        return Optional.of(Pair.of(user.get(), token));
    }
}
