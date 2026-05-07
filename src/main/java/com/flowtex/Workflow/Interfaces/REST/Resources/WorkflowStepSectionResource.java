package com.flowtex.Workflow.Interfaces.REST.Resources;

public record WorkflowStepSectionResource(
        Long id,
        int position,
        String sectionKind,
        String label,
        boolean required,
        String config
) {
}
