package com.flowtex.FormBuilder.Domain.Model.Commands;

import java.util.List;

public record CreateFormCommand(
        String title,
        String description,
        String context,
        Long ownerId,
        List<FieldDescriptor> fields
) {
}
