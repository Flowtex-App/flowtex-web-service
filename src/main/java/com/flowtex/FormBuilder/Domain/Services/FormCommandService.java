package com.flowtex.FormBuilder.Domain.Services;

import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import com.flowtex.FormBuilder.Domain.Model.Commands.CreateFormCommand;
import com.flowtex.FormBuilder.Domain.Model.Commands.DeleteFormCommand;
import com.flowtex.FormBuilder.Domain.Model.Commands.PublishFormCommand;
import com.flowtex.FormBuilder.Domain.Model.Commands.UpdateFormCommand;

import java.util.Optional;

public interface FormCommandService {
    Optional<Form> handle(CreateFormCommand command);
    Optional<Form> handle(UpdateFormCommand command);
    Optional<Form> handle(PublishFormCommand command);
    void handle(DeleteFormCommand command);
}
