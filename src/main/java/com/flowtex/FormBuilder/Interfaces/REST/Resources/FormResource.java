package com.flowtex.FormBuilder.Interfaces.REST.Resources;

import java.time.LocalDateTime;
import java.util.List;

public record FormResource(
        Long id,
        String title,
        String description,
        String context,
        String status,
        int version,
        Long ownerId,
        List<FormFieldResource> fields,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
