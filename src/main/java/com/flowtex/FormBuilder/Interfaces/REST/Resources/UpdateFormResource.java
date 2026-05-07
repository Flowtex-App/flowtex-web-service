package com.flowtex.FormBuilder.Interfaces.REST.Resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateFormResource(
        @NotBlank @Size(max = 160) String title,
        @Size(max = 500) String description,
        @Size(max = 500) String context,
        @Valid List<FieldDescriptorResource> fields
) {
}
