package com.flowtex.IAM.Interfaces.REST.Controllers;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Model.Commands.UpdateUserRolesCommand;
import com.flowtex.IAM.Domain.Model.ValueObjects.Area;
import com.flowtex.IAM.Domain.Model.ValueObjects.Position;
import com.flowtex.IAM.Domain.Model.ValueObjects.Roles;
import com.flowtex.IAM.Domain.Services.UserCommandService;
import com.flowtex.IAM.Domain.Services.UserQueryService;
import com.flowtex.IAM.Interfaces.REST.Resources.UpdateUserRolesResource;
import com.flowtex.IAM.Interfaces.REST.Resources.UserResource;
import com.flowtex.IAM.Interfaces.REST.Transform.AuthenticatedUserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
@Tag(name = "Users")
public class UsersController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @GetMapping
    @Operation(summary = "List users with optional text/area/position filters")
    public ResponseEntity<List<UserResource>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String position) {

        List<User> users;
        if (q != null && !q.isBlank()) {
            users = userQueryService.searchByText(q);
        } else if (area != null && position != null) {
            users = userQueryService.getByAreaAndPosition(Area.valueOf(area.toUpperCase()),
                    Position.valueOf(position.toUpperCase()));
        } else if (area != null) {
            users = userQueryService.getByArea(Area.valueOf(area.toUpperCase()));
        } else {
            users = userQueryService.getAllUsers();
        }

        return ResponseEntity.ok(users.stream()
                .map(AuthenticatedUserResourceFromEntityAssembler::toUserResourceFromEntity)
                .toList());
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

    @GetMapping("/by-employee-code/{code}")
    @Operation(summary = "Find a user by employee code (Cxxxxx)")
    public ResponseEntity<UserResource> getByEmployeeCode(@PathVariable String code) {
        return userQueryService.getByEmployeeCode(code)
                .map(AuthenticatedUserResourceFromEntityAssembler::toUserResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Replace the role set of a user (admin only)")
    public ResponseEntity<UserResource> updateRoles(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateUserRolesResource body) {
        List<Roles> roles = body.roles().stream()
                .map(r -> Roles.valueOf(r.startsWith("ROLE_") ? r : "ROLE_" + r))
                .toList();
        return userCommandService.handle(new UpdateUserRolesCommand(id, roles))
                .map(AuthenticatedUserResourceFromEntityAssembler::toUserResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
