package com.flowtex.FormBuilder.Domain.Model.Commands;

public record SuggestFieldNamesCommand(
        String formContext,
        String formTitle,
        Integer maxSuggestions
) {
}
