package com.flowtex.FormBuilder.Interfaces.REST.Controllers;

import com.flowtex.FormBuilder.Application.Internal.OutboundServices.FieldSuggestionService;
import com.flowtex.FormBuilder.Domain.Model.Commands.SuggestFieldNamesCommand;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.FieldSuggestionResource;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.SuggestFieldNamesResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/forms/suggestions", produces = "application/json")
@Tag(name = "AI Suggestions", description = "AI-powered field name suggestions")
public class FieldSuggestionsController {

    private final FieldSuggestionService suggestionService;

    public FieldSuggestionsController(FieldSuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @PostMapping("/fields")
    @Operation(summary = "Suggest form field names from a context description")
    public ResponseEntity<List<FieldSuggestionResource>> suggestFields(
            @RequestBody SuggestFieldNamesResource resource) {

        SuggestFieldNamesCommand command = new SuggestFieldNamesCommand(
                resource.formContext(),
                resource.formTitle(),
                resource.maxSuggestions());

        List<FieldSuggestionResource> response = suggestionService.suggest(command).stream()
                .map(s -> new FieldSuggestionResource(s.label(), s.fieldKey(), s.fieldType(), s.rationale()))
                .toList();

        return ResponseEntity.ok(response);
    }
}
