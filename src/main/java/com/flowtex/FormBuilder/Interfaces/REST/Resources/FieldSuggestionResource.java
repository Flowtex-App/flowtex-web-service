package com.flowtex.FormBuilder.Interfaces.REST.Resources;

public record FieldSuggestionResource(
        String label,
        String fieldKey,
        String fieldType,
        String rationale
) {
}
