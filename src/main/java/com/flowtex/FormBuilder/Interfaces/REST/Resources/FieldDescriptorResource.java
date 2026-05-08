package com.flowtex.FormBuilder.Interfaces.REST.Resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
        @Min(1) @Max(12) Integer width,
        @Min(1) @Max(12) Integer colStart,
        @Min(1) Integer rowStart,
        @Min(1) Integer rowSpan,
        String options
) {
}
