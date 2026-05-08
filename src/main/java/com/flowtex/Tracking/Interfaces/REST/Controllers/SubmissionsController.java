package com.flowtex.Tracking.Interfaces.REST.Controllers;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Repositories.UserRepository;
import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.Commands.CreateSubmissionCommand;
import com.flowtex.Tracking.Domain.Model.Commands.DecideStepCommand;
import com.flowtex.Tracking.Domain.Model.Commands.UpdateSubmissionDataCommand;
import com.flowtex.Tracking.Domain.Model.ValueObjects.Decision;
import com.flowtex.Tracking.Domain.Services.SubmissionCommandService;
import com.flowtex.Tracking.Domain.Services.SubmissionQueryService;
import com.flowtex.Tracking.Interfaces.REST.Resources.CreateSubmissionResource;
import com.flowtex.Tracking.Interfaces.REST.Resources.DecideStepResource;
import com.flowtex.Tracking.Interfaces.REST.Resources.SubmissionResource;
import com.flowtex.Tracking.Interfaces.REST.Resources.UpdateSubmissionDataResource;
import com.flowtex.Tracking.Interfaces.REST.Transform.SubmissionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/v1/submissions", produces = "application/json")
@Tag(name = "Submissions")
public class SubmissionsController {

    private final SubmissionCommandService commandService;
    private final SubmissionQueryService queryService;
    private final UserRepository userRepository;

    public SubmissionsController(SubmissionCommandService commandService,
                                 SubmissionQueryService queryService,
                                 UserRepository userRepository) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Lista solicitudes (mías, asignadas o todas según ?scope=)")
    public ResponseEntity<List<SubmissionResource>> list(
            @RequestParam(defaultValue = "mine") String scope,
            @AuthenticationPrincipal UserDetails principal) {
        User me = currentUser(principal);
        List<Submission> data = switch (scope) {
            case "assigned" -> queryService.getAssignedTo(me.getId());
            case "all"      -> queryService.getAll();
            default         -> queryService.getMine(me.getId());
        };
        return ResponseEntity.ok(data.stream()
                .map(s -> SubmissionResourceFromEntityAssembler.toResource(s, userRepository))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResource> getOne(@PathVariable Long id) {
        return queryService.getById(id)
                .map(s -> SubmissionResourceFromEntityAssembler.toResource(s, userRepository))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-ticket/{ticket}")
    public ResponseEntity<SubmissionResource> getByTicket(@PathVariable String ticket) {
        return queryService.getByTicket(ticket)
                .map(s -> SubmissionResourceFromEntityAssembler.toResource(s, userRepository))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Envía un nuevo formulario relleno y arranca el workflow")
    public ResponseEntity<SubmissionResource> create(
            @Valid @RequestBody CreateSubmissionResource body,
            @AuthenticationPrincipal UserDetails principal) {
        User me = currentUser(principal);
        Submission saved = commandService.handle(new CreateSubmissionCommand(
                        body.formId(), me.getId(), body.data() == null ? java.util.Map.of() : body.data()))
                .orElseThrow(() -> new IllegalStateException("No se pudo crear la solicitud"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubmissionResourceFromEntityAssembler.toResource(saved, userRepository));
    }

    @PutMapping("/{id}/data")
    @Operation(summary = "Edita los datos de una solicitud (solicitante, mientras esté abierta o devuelta)")
    public ResponseEntity<SubmissionResource> updateData(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubmissionDataResource body,
            @AuthenticationPrincipal UserDetails principal) {
        User me = currentUser(principal);
        Submission saved = commandService.handle(
                        new UpdateSubmissionDataCommand(id, me.getId(), body.data()))
                .orElseThrow(() -> new IllegalStateException("No se pudo actualizar"));
        return ResponseEntity.ok(SubmissionResourceFromEntityAssembler.toResource(saved, userRepository));
    }

    @PostMapping("/{id}/steps/{execId}/decide")
    @Operation(summary = "Decisión sobre un paso (APPROVE, REJECT, RETURN)")
    public ResponseEntity<SubmissionResource> decide(
            @PathVariable Long id,
            @PathVariable Long execId,
            @Valid @RequestBody DecideStepResource body,
            @AuthenticationPrincipal UserDetails principal) {
        User me = currentUser(principal);
        Decision decision = Decision.valueOf(body.decision().toUpperCase());
        Submission saved = commandService.handle(
                        new DecideStepCommand(id, execId, me.getId(), decision, body.comments()))
                .orElseThrow(() -> new IllegalStateException("No se pudo decidir"));
        return ResponseEntity.ok(SubmissionResourceFromEntityAssembler.toResource(saved, userRepository));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        commandService.cancel(id, currentUser(principal).getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resubmit")
    public ResponseEntity<SubmissionResource> resubmit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        User me = currentUser(principal);
        Submission saved = commandService.resubmit(id, me.getId())
                .orElseThrow(() -> new IllegalStateException("No se pudo reenviar"));
        return ResponseEntity.ok(SubmissionResourceFromEntityAssembler.toResource(saved, userRepository));
    }

    private User currentUser(UserDetails principal) {
        if (principal == null) throw new IllegalStateException("No autenticado");
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }
}
