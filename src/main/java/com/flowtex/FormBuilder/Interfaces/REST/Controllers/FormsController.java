package com.flowtex.FormBuilder.Interfaces.REST.Controllers;

import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import com.flowtex.FormBuilder.Domain.Model.Commands.DeleteFormCommand;
import com.flowtex.FormBuilder.Domain.Model.Commands.PublishFormCommand;
import com.flowtex.FormBuilder.Domain.Model.Queries.GetAllFormsQuery;
import com.flowtex.FormBuilder.Domain.Model.Queries.GetFormByIdQuery;
import com.flowtex.FormBuilder.Domain.Repositories.FormRepository;
import com.flowtex.FormBuilder.Domain.Services.FormCommandService;
import com.flowtex.FormBuilder.Domain.Services.FormQueryService;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.CreateFormResource;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.FormResource;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.UpdateFormResource;
import com.flowtex.FormBuilder.Interfaces.REST.Transform.CreateFormCommandFromResourceAssembler;
import com.flowtex.FormBuilder.Interfaces.REST.Transform.FormResourceFromEntityAssembler;
import com.flowtex.IAM.Interfaces.ACL.IamContextFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/forms", produces = "application/json")
@Tag(name = "Forms", description = "FormBuilder endpoints")
public class FormsController {

    private final FormCommandService commandService;
    private final FormQueryService queryService;
    private final IamContextFacade iamContextFacade;
    private final FormRepository formRepository;

    public FormsController(FormCommandService commandService,
                           FormQueryService queryService,
                           IamContextFacade iamContextFacade,
                           FormRepository formRepository) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.iamContextFacade = iamContextFacade;
        this.formRepository = formRepository;
    }

    @GetMapping
    @Operation(summary = "List all forms")
    public ResponseEntity<List<FormResource>> list() {
        List<FormResource> forms = queryService.handle(new GetAllFormsQuery()).stream()
                .map(FormResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(forms);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get form by id")
    public ResponseEntity<FormResource> getById(@PathVariable Long id) {
        return queryService.handle(new GetFormByIdQuery(id))
                .map(FormResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a form")
    public ResponseEntity<FormResource> create(@Valid @RequestBody CreateFormResource resource,
                                               @AuthenticationPrincipal UserDetails principal) {
        Long ownerId = iamContextFacade.fetchUserIdByUsername(principal.getUsername());
        Form created = commandService.handle(CreateFormCommandFromResourceAssembler.toCommand(resource, ownerId))
                .orElseThrow(() -> new IllegalStateException("Could not create form"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FormResourceFromEntityAssembler.toResourceFromEntity(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a form")
    public ResponseEntity<FormResource> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateFormResource resource) {
        Form updated = commandService.handle(CreateFormCommandFromResourceAssembler.toUpdateCommand(id, resource))
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));
        return ResponseEntity.ok(FormResourceFromEntityAssembler.toResourceFromEntity(updated));
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish a form")
    public ResponseEntity<FormResource> publish(@PathVariable Long id) {
        Form published = commandService.handle(new PublishFormCommand(id))
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));
        return ResponseEntity.ok(FormResourceFromEntityAssembler.toResourceFromEntity(published));
    }

    @PutMapping("/{id}/workflow")
    @Operation(summary = "Link a workflow to a form (or unlink with workflowId=null)")
    @Transactional
    public ResponseEntity<FormResource> linkWorkflow(@PathVariable Long id,
                                                     @RequestBody Map<String, Long> body) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));
        form.linkWorkflow(body.get("workflowId"));
        Form saved = formRepository.save(form);
        return ResponseEntity.ok(FormResourceFromEntityAssembler.toResourceFromEntity(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a form")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandService.handle(new DeleteFormCommand(id));
        return ResponseEntity.noContent().build();
    }
}
