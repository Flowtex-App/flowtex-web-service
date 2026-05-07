package com.flowtex.FormBuilder.Application.Internal.OutboundServices;

import com.flowtex.FormBuilder.Domain.Model.Commands.SuggestFieldNamesCommand;

import java.util.List;

public interface FieldSuggestionService {
    List<FieldSuggestion> suggest(SuggestFieldNamesCommand command);

    record FieldSuggestion(String label, String fieldKey, String fieldType, String rationale) {
    }
}
