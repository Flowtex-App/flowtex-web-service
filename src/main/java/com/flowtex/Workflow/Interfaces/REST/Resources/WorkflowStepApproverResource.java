package com.flowtex.Workflow.Interfaces.REST.Resources;

public record WorkflowStepApproverResource(
        Long id,
        int position,
        String kind,
        Long userId,
        String area,
        String userPosition,
        String role
) {
}
