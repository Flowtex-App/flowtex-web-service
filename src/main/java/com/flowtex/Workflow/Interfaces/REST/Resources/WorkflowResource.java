package com.flowtex.Workflow.Interfaces.REST.Resources;

import java.time.LocalDateTime;
import java.util.List;

public record WorkflowResource(
        Long id,
        String name,
        String description,
        String status,
        Long ownerId,
        List<WorkflowStepResource> steps,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
