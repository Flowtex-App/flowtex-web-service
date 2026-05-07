package com.flowtex.FormBuilder.Domain.Services;

import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import com.flowtex.FormBuilder.Domain.Model.Queries.GetAllFormsQuery;
import com.flowtex.FormBuilder.Domain.Model.Queries.GetFormByIdQuery;

import java.util.List;
import java.util.Optional;

public interface FormQueryService {
    List<Form> handle(GetAllFormsQuery query);
    Optional<Form> handle(GetFormByIdQuery query);
}
