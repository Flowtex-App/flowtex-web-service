package com.flowtex.FormBuilder.Interfaces.REST.Resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FieldDescriptorResource(
        @NotBlank String label,
        @NotBlank String fieldKey,
        @NotBlank String fieldType,
        @NotNull Boolean required,
        String placeholder,
        String helpText,
        @NotNull Integer position,
        String options
) {
}
