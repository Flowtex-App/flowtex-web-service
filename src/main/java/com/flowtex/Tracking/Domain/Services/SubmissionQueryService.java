package com.flowtex.Tracking.Domain.Services;

import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;

import java.util.List;
import java.util.Optional;

public interface SubmissionQueryService {
    Optional<Submission> getById(Long id);
    Optional<Submission> getByTicket(String ticket);
    List<Submission> getMine(Long userId);
    List<Submission> getAssignedTo(Long userId);
    List<Submission> getAll();
}
