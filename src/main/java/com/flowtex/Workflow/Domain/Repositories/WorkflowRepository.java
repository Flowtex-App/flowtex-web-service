package com.flowtex.Workflow.Domain.Repositories;

import com.flowtex.Workflow.Domain.Model.Aggregates.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
}
