package com.flowtex.Workflow.Interfaces.REST.Controllers;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Repositories.UserRepository;
import com.flowtex.Workflow.Domain.Model.Aggregates.Workflow;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStep;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStepApprover;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStepSection;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStepTransition;
import com.flowtex.Workflow.Domain.Model.ValueObjects.ApproverKind;
import com.flowtex.Workflow.Domain.Model.ValueObjects.SectionKind;
import com.flowtex.Workflow.Domain.Model.ValueObjects.StepMode;
import com.flowtex.Workflow.Domain.Model.ValueObjects.TransitionCondition;
import com.flowtex.Workflow.Domain.Repositories.WorkflowRepository;
import com.flowtex.Workflow.Interfaces.REST.Resources.SaveWorkflowResource;
import com.flowtex.Workflow.Interfaces.REST.Resources.WorkflowResource;
import com.flowtex.Workflow.Interfaces.REST.Transform.WorkflowResourceFromEntityAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowsController {

    private final WorkflowRepository workflowRepository;
    private final UserRepository userRepository;

    public WorkflowsController(WorkflowRepository workflowRepository, UserRepository userRepository) {
        this.workflowRepository = workflowRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<WorkflowResource> list() {
        return workflowRepository.findAll().stream()
                .sorted(Comparator.comparing(Workflow::getId))
                .map(WorkflowResourceFromEntityAssembler::toResource)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowResource> get(@PathVariable Long id) {
        return workflowRepository.findById(id)
                .map(WorkflowResourceFromEntityAssembler::toResource)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<WorkflowResource> create(
            @RequestBody SaveWorkflowResource body,
            @AuthenticationPrincipal UserDetails principal
    ) {
        User owner = currentUser(principal);
        Workflow w = new Workflow(body.name(), body.description(), owner);
        applySteps(w, body.steps());
        Workflow saved = workflowRepository.save(w);
        return ResponseEntity.ok(WorkflowResourceFromEntityAssembler.toResource(saved));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<WorkflowResource> update(
            @PathVariable Long id,
            @RequestBody SaveWorkflowResource body
    ) {
        Workflow w = workflowRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("workflow not found"));
        w.update(body.name(), body.description());
        applySteps(w, body.steps());
        Workflow saved = workflowRepository.save(w);
        return ResponseEntity.ok(WorkflowResourceFromEntityAssembler.toResource(saved));
    }

    @PostMapping("/{id}/publish")
    @Transactional
    public ResponseEntity<WorkflowResource> publish(@PathVariable Long id) {
        Workflow w = workflowRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("workflow not found"));
        w.publish();
        return ResponseEntity.ok(WorkflowResourceFromEntityAssembler.toResource(workflowRepository.save(w)));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!workflowRepository.existsById(id)) return ResponseEntity.notFound().build();
        workflowRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────────────────────────────

    /**
     * Persist the steps + sections + transitions defined in the payload.
     *
     * Transitions reference target steps by `toStepRef`, which can be either a
     * real DB id (as a string) or a client tempId such as "tmp-3". We do this
     * in two passes:
     *   1. Build all WorkflowStep entities and store them in a map keyed by
     *      the ref the client used (tempId or id).
     *   2. After the steps are wired into the aggregate (which assigns ids on
     *      flush), resolve transition refs against that map.
     */
    private void applySteps(Workflow w, List<SaveWorkflowResource.SaveStepResource> stepBodies) {
        if (stepBodies == null) {
            w.replaceSteps(new ArrayList<>());
            return;
        }

        // Pass 1: build steps in order
        List<WorkflowStep> steps = new ArrayList<>();
        Map<String, WorkflowStep> byRef = new HashMap<>();
        int idx = 0;
        for (SaveWorkflowResource.SaveStepResource sb : stepBodies) {
            StepMode mode = parseEnum(sb.mode(), StepMode.SEQUENTIAL);
            WorkflowStep step = new WorkflowStep(
                    idx++, sb.label(), sb.role(), sb.slaHours(), mode, sb.description(),
                    sb.canvasX(), sb.canvasY(), sb.color()
            );

            // Sections
            List<WorkflowStepSection> sections = new ArrayList<>();
            if (sb.sections() != null) {
                int sIdx = 0;
                for (SaveWorkflowResource.SaveSectionResource secB : sb.sections()) {
                    SectionKind kind = parseEnum(secB.sectionKind(), SectionKind.COMMENTS);
                    sections.add(new WorkflowStepSection(kind, secB.label(), secB.required(), sIdx++, secB.config()));
                }
            }
            step.replaceSections(sections);

            // Approvers
            List<WorkflowStepApprover> approvers = new ArrayList<>();
            if (sb.approvers() != null) {
                int aIdx = 0;
                for (SaveWorkflowResource.SaveApproverResource ab : sb.approvers()) {
                    ApproverKind kind = parseEnum(ab.kind(), ApproverKind.ROLE);
                    approvers.add(new WorkflowStepApprover(
                            aIdx++, kind, ab.userId(), ab.area(), ab.userPosition(), ab.role()
                    ));
                }
            }
            step.replaceApprovers(approvers);

            steps.add(step);
            if (sb.tempId() != null) byRef.put(sb.tempId(), step);
            if (sb.id() != null) byRef.put(String.valueOf(sb.id()), step);
        }

        // Wire steps into the aggregate so their ids materialise on flush.
        w.replaceSteps(steps);

        // Pass 2: build transitions, resolving toStepRef against the map.
        for (int i = 0; i < stepBodies.size(); i++) {
            SaveWorkflowResource.SaveStepResource sb = stepBodies.get(i);
            WorkflowStep step = steps.get(i);
            List<WorkflowStepTransition> transitions = new ArrayList<>();
            if (sb.transitions() != null) {
                int tIdx = 0;
                for (SaveWorkflowResource.SaveTransitionResource tb : sb.transitions()) {
                    TransitionCondition cond = parseEnum(tb.conditionKind(), TransitionCondition.ALWAYS);
                    WorkflowStepTransition t = new WorkflowStepTransition(
                            cond, tb.label(), tIdx++, tb.config(),
                            tb.sourceHandle(), tb.targetHandle()
                    );
                    if (tb.toStepRef() != null) {
                        WorkflowStep target = byRef.get(tb.toStepRef());
                        if (target != null) t.attachTo(target);
                    }
                    transitions.add(t);
                }
            }
            step.replaceTransitions(transitions);
        }
    }

    private <E extends Enum<E>> E parseEnum(String raw, E fallback) {
        if (raw == null) return fallback;
        try {
            @SuppressWarnings("unchecked")
            Class<E> cls = (Class<E>) fallback.getClass();
            return Enum.valueOf(cls, raw);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private User currentUser(UserDetails principal) {
        if (principal == null) {
            return userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("no users available"));
        }
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new NoSuchElementException("user not found"));
    }
}
