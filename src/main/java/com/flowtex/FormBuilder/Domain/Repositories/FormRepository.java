package com.flowtex.FormBuilder.Domain.Repositories;

import com.flowtex.FormBuilder.Domain.Model.Aggregates.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByOwnerId(Long ownerId);
}
