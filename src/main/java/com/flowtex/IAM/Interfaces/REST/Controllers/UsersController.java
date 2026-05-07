package com.flowtex.IAM.Interfaces.REST.Controllers;

import com.flowtex.IAM.Domain.Services.UserQueryService;
import com.flowtex.IAM.Interfaces.REST.Resources.UserResource;
import com.flowtex.IAM.Interfaces.REST.Transform.AuthenticatedUserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
@Tag(name = "Users")
public class UsersController {

    private final UserQueryService userQueryService;

    public UsersController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping
    @Operation(summary = "List all users")
    public ResponseEntity<List<UserResource>> list() {
        List<UserResource> users = userQueryService.getAllUsers().stream()
                .map(AuthenticatedUserResourceFromEntityAssembler::toUserResourceFromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    @Operation(summary = "Current authenticated user")
    public ResponseEntity<UserResource> me(@AuthenticationPrincipal UserDetails principal) {
        return userQueryService.getByUsername(principal.getUsername())
                .map(AuthenticatedUserResourceFromEntityAssembler::toUserResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<UserResource> getById(@PathVariable Long id) {
        return userQueryService.getById(id)
                .map(AuthenticatedUserResourceFromEntityAssembler::toUserResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
