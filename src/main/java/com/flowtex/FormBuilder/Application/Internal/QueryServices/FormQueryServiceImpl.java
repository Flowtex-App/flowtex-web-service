package com.flowtex.FormBuilder.Application.Internal.QueryServices;

import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import com.flowtex.FormBuilder.Domain.Model.Queries.GetAllFormsQuery;
import com.flowtex.FormBuilder.Domain.Model.Queries.GetFormByIdQuery;
import com.flowtex.FormBuilder.Domain.Repositories.FormRepository;
import com.flowtex.FormBuilder.Domain.Services.FormQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FormQueryServiceImpl implements FormQueryService {

    private final FormRepository formRepository;

    public FormQueryServiceImpl(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Form> handle(GetAllFormsQuery query) {
        return formRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Form> handle(GetFormByIdQuery query) {
        return formRepository.findById(query.formId());
    }
}
