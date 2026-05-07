package com.flowtex.Workflow.Interfaces.REST.Resources;

public record WorkflowStepTransitionResource(
        Long id,
        Long fromStepId,
        Long toStepId,
        String conditionKind,
        String label,
        int position,
        String config,
        String sourceHandle,
        String targetHandle
) {
}
