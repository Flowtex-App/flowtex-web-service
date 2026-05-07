package com.flowtex.FormBuilder.Interfaces.REST.Transform;

import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import com.flowtex.FormBuilder.Domain.Model.Entities.FormField;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.FormFieldResource;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.FormResource;

import java.util.List;

public class FormResourceFromEntityAssembler {

    public static FormResource toResourceFromEntity(Form form) {
        List<FormFieldResource> fields = form.getFields().stream()
                .map(FormResourceFromEntityAssembler::toFieldResource)
                .toList();

        return new FormResource(
                form.getId(),
                form.getTitle(),
                form.getDescription(),
                form.getContext(),
                form.getStatus().name(),
                form.getVersion(),
                form.getOwnerId(),
                fields,
                form.getCreatedAt(),
                form.getUpdatedAt());
    }

    private static FormFieldResource toFieldResource(FormField field) {
        return new FormFieldResource(
                field.getId(),
                field.getLabel(),
                field.getFieldKey(),
                field.getFieldType().name(),
                field.isRequired(),
                field.getPlaceholder(),
                field.getHelpText(),
                field.getPosition(),
                field.getWidth(),
                field.getOptions());
    }
}
