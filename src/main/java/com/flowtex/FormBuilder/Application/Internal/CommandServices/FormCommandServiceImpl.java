package com.flowtex.FormBuilder.Application.Internal.CommandServices;

import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import com.flowtex.FormBuilder.Domain.Model.Commands.*;
import com.flowtex.FormBuilder.Domain.Model.Entities.FormField;
import com.flowtex.FormBuilder.Domain.Repositories.FormRepository;
import com.flowtex.FormBuilder.Domain.Services.FormCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FormCommandServiceImpl implements FormCommandService {

    private final FormRepository formRepository;

    public FormCommandServiceImpl(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    @Override
    @Transactional
    public Optional<Form> handle(CreateFormCommand command) {
        Form form = new Form(command.title(), command.description(), command.context(), command.ownerId());
        if (command.fields() != null) {
            form.replaceFields(toFormFields(command.fields()));
        }
        return Optional.of(formRepository.save(form));
    }

    @Override
    @Transactional
    public Optional<Form> handle(UpdateFormCommand command) {
        Form form = formRepository.findById(command.formId())
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + command.formId()));
        form.update(command.title(), command.description(), command.context());
        if (command.fields() != null) {
            form.replaceFields(toFormFields(command.fields()));
        }
        return Optional.of(formRepository.save(form));
    }

    @Override
    @Transactional
    public Optional<Form> handle(PublishFormCommand command) {
        Form form = formRepository.findById(command.formId())
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + command.formId()));
        form.publish();
        return Optional.of(formRepository.save(form));
    }

    @Override
    @Transactional
    public void handle(DeleteFormCommand command) {
        formRepository.deleteById(command.formId());
    }

    private List<FormField> toFormFields(List<FieldDescriptor> descriptors) {
        return descriptors.stream()
                .map(d -> new FormField(
                        d.label(), d.fieldKey(), d.fieldType(), d.required(),
                        d.placeholder(), d.helpText(), d.position(), d.width(), d.options()))
                .toList();
    }
}
