package com.flowtex.FormBuilder.Interfaces.REST.Resources;

public record SuggestFieldNamesResource(
        String formTitle,
        String formContext,
        Integer maxSuggestions
) {
}
