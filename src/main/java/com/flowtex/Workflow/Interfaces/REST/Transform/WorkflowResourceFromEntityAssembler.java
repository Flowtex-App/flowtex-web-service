package com.flowtex.Workflow.Interfaces.REST.Transform;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.Workflow.Domain.Model.Aggregates.Workflow;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStep;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStepApprover;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStepSection;
import com.flowtex.Workflow.Domain.Model.Entities.WorkflowStepTransition;
import com.flowtex.Workflow.Interfaces.REST.Resources.WorkflowResource;
import com.flowtex.Workflow.Interfaces.REST.Resources.WorkflowStepApproverResource;
import com.flowtex.Workflow.Interfaces.REST.Resources.WorkflowStepResource;
import com.flowtex.Workflow.Interfaces.REST.Resources.WorkflowStepSectionResource;
import com.flowtex.Workflow.Interfaces.REST.Resources.WorkflowStepTransitionResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class WorkflowResourceFromEntityAssembler {

    private WorkflowResourceFromEntityAssembler() {}

    public static WorkflowResource toResource(Workflow w) {
        return toResource(w, Collections.emptyMap());
    }

    public static WorkflowResource toResource(Workflow w, Map<Long, User> usersById) {
        List<WorkflowStepResource> steps = w.getOrderedSteps().stream()
                .map(s -> toStepResource(s, usersById))
                .collect(Collectors.toList());
        return new WorkflowResource(
                w.getId(),
                w.getName(),
                w.getDescription(),
                w.getStatus().name(),
                w.getOwner() != null ? w.getOwner().getId() : null,
                steps,
                w.getCreatedAt(),
                w.getUpdatedAt()
        );
    }

    private static WorkflowStepResource toStepResource(WorkflowStep s, Map<Long, User> usersById) {
        List<WorkflowStepSectionResource> sections = s.getOrderedSections().stream()
                .map(WorkflowResourceFromEntityAssembler::toSectionResource)
                .collect(Collectors.toList());
        List<WorkflowStepTransitionResource> transitions = s.getOrderedTransitions().stream()
                .map(WorkflowResourceFromEntityAssembler::toTransitionResource)
                .collect(Collectors.toList());
        List<WorkflowStepApproverResource> approvers = s.getOrderedApprovers().stream()
                .map(a -> toApproverResource(a, usersById))
                .collect(Collectors.toList());
        return new WorkflowStepResource(
                s.getId(),
                s.getPosition(),
                s.getLabel(),
                s.getRole(),
                s.getSlaHours(),
                s.getMode().name(),
                s.getDescription(),
                s.getCanvasX(),
                s.getCanvasY(),
                s.getColor(),
                sections,
                transitions,
                approvers
        );
    }

    private static WorkflowStepSectionResource toSectionResource(WorkflowStepSection sec) {
        return new WorkflowStepSectionResource(
                sec.getId(),
                sec.getPosition(),
                sec.getSectionKind().name(),
                sec.getLabel(),
                sec.isRequired(),
                sec.getConfig()
        );
    }

    private static WorkflowStepTransitionResource toTransitionResource(WorkflowStepTransition t) {
        return new WorkflowStepTransitionResource(
                t.getId(),
                t.getFromStep() != null ? t.getFromStep().getId() : null,
                t.getToStep() != null ? t.getToStep().getId() : null,
                t.getConditionKind().name(),
                t.getLabel(),
                t.getPosition(),
                t.getConfig(),
                t.getSourceHandle(),
                t.getTargetHandle()
        );
    }

    private static WorkflowStepApproverResource toApproverResource(WorkflowStepApprover a, Map<Long, User> usersById) {
        User u = a.getUserId() != null ? usersById.get(a.getUserId()) : null;
        return new WorkflowStepApproverResource(
                a.getId(),
                a.getPosition(),
                a.getKind().name(),
                a.getUserId(),
                a.getArea(),
                a.getUserPosition(),
                a.getRole(),
                u != null ? u.getFullName() : null,
                u != null ? u.getEmployeeCode() : null
        );
    }
}
