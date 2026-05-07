package com.flowtex.FormBuilder.Interfaces.REST.Resources;

public record FormFieldResource(
        Long id,
        String label,
        String fieldKey,
        String fieldType,
        boolean required,
        String placeholder,
        String helpText,
        int position,
        String options
) {
}
