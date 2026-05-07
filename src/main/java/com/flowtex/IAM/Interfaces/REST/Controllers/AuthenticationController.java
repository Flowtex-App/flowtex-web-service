package com.flowtex.IAM.Interfaces.REST.Controllers;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Services.UserCommandService;
import com.flowtex.IAM.Interfaces.REST.Resources.AuthenticatedUserResource;
import com.flowtex.IAM.Interfaces.REST.Resources.SignInResource;
import com.flowtex.IAM.Interfaces.REST.Resources.SignUpResource;
import com.flowtex.IAM.Interfaces.REST.Resources.UserResource;
import com.flowtex.IAM.Interfaces.REST.Transform.AuthenticatedUserResourceFromEntityAssembler;
import com.flowtex.IAM.Interfaces.REST.Transform.SignInCommandFromResourceAssembler;
import com.flowtex.IAM.Interfaces.REST.Transform.SignUpCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = "application/json")
@Tag(name = "Authentication", description = "Sign-up and sign-in endpoints")
public class AuthenticationController {

    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResource> signUp(@Valid @RequestBody SignUpResource resource) {
        User created = userCommandService.handle(SignUpCommandFromResourceAssembler.toCommand(resource))
                .orElseThrow(() -> new IllegalStateException("Could not create user"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthenticatedUserResourceFromEntityAssembler.toUserResourceFromEntity(created));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign in and obtain a JWT")
    public ResponseEntity<AuthenticatedUserResource> signIn(@Valid @RequestBody SignInResource resource) {
        Pair<User, String> result = userCommandService.handle(SignInCommandFromResourceAssembler.toCommand(resource))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        return ResponseEntity.ok(AuthenticatedUserResourceFromEntityAssembler
                .toResourceFromEntity(result.getFirst(), result.getSecond()));
    }
}
