package com.flowtex.Workflow.Interfaces.REST.Resources;

import java.util.List;

public record WorkflowStepResource(
        Long id,
        int position,
        String label,
        String role,
        int slaHours,
        String mode,
        String description,
        int canvasX,
        int canvasY,
        String color,
        List<WorkflowStepSectionResource> sections,
        List<WorkflowStepTransitionResource> transitions,
        List<WorkflowStepApproverResource> approvers
) {
}
