package com.flowtex.FormBuilder.Domain.Model.Commands;

import java.util.List;

public record UpdateFormCommand(
        Long formId,
        String title,
        String description,
        String context,
        List<FieldDescriptor> fields
) {
}
