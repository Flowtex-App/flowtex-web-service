package com.flowtex.FormBuilder.Interfaces.REST.Transform;

import com.flowtex.FormBuilder.Domain.Model.Commands.CreateFormCommand;
import com.flowtex.FormBuilder.Domain.Model.Commands.FieldDescriptor;
import com.flowtex.FormBuilder.Domain.Model.Commands.UpdateFormCommand;
import com.flowtex.FormBuilder.Domain.Model.ValueObjects.FieldType;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.CreateFormResource;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.FieldDescriptorResource;
import com.flowtex.FormBuilder.Interfaces.REST.Resources.UpdateFormResource;

import java.util.List;
import java.util.Optional;

public class CreateFormCommandFromResourceAssembler {

    public static CreateFormCommand toCommand(CreateFormResource resource, Long ownerId) {
        return new CreateFormCommand(
                resource.title(),
                resource.description(),
                resource.context(),
                ownerId,
                toFieldDescriptors(resource.fields()));
    }

    public static UpdateFormCommand toUpdateCommand(Long formId, UpdateFormResource resource) {
        return new UpdateFormCommand(
                formId,
                resource.title(),
                resource.description(),
                resource.context(),
                toFieldDescriptors(resource.fields()));
    }

    private static List<FieldDescriptor> toFieldDescriptors(List<FieldDescriptorResource> resources) {
        return Optional.ofNullable(resources).orElse(List.of()).stream()
                .map(r -> new FieldDescriptor(
                        r.label(),
                        r.fieldKey(),
                        FieldType.valueOf(r.fieldType()),
                        Boolean.TRUE.equals(r.required()),
                        r.placeholder(),
                        r.helpText(),
                        r.position() != null ? r.position() : 0,
                        r.width() != null ? Math.max(1, Math.min(12, r.width())) : 12,
                        r.options()))
                .toList();
    }
}
