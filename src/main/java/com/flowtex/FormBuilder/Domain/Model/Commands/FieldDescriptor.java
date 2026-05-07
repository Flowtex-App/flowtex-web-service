package com.flowtex.FormBuilder.Domain.Model.Commands;

import com.flowtex.FormBuilder.Domain.Model.ValueObjects.FieldType;

public record FieldDescriptor(
        String label,
        String fieldKey,
        FieldType fieldType,
        boolean required,
        String placeholder,
        String helpText,
        int position,
        int width,
        String options
) {
}
