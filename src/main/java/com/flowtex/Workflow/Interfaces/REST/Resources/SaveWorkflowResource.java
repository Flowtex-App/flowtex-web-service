package com.flowtex.Workflow.Interfaces.REST.Resources;

import java.util.List;

public record SaveWorkflowResource(
        String name,
        String description,
        List<SaveStepResource> steps
) {
    public record SaveStepResource(
            String tempId,
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
            List<SaveSectionResource> sections,
            List<SaveTransitionResource> transitions
    ) {}

    public record SaveSectionResource(
            Long id,
            int position,
            String sectionKind,
            String label,
            boolean required,
            String config
    ) {}

    public record SaveTransitionResource(
            Long id,
            String toStepRef,
            String conditionKind,
            String label,
            int position,
            String config,
            String sourceHandle,
            String targetHandle
    ) {}
}
