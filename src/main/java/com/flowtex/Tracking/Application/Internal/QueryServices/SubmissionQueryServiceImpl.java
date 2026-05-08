package com.flowtex.Tracking.Application.Internal.QueryServices;

import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Repositories.SubmissionRepository;
import com.flowtex.Tracking.Domain.Services.SubmissionQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionQueryServiceImpl implements SubmissionQueryService {

    private final SubmissionRepository submissionRepository;

    public SubmissionQueryServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Submission> getById(Long id) { return submissionRepository.findById(id); }

    @Override
    @Transactional(readOnly = true)
    public Optional<Submission> getByTicket(String ticket) {
        return submissionRepository.findByTicketCode(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Submission> getMine(Long userId) {
        return submissionRepository.findBySubmitterIdOrderBySubmittedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Submission> getAssignedTo(Long userId) {
        // Submissions con al menos un step PENDING/IN_PROGRESS asignado al usuario
        return submissionRepository.findAll().stream()
                .filter(s -> s.getOrderedExecutions().stream().anyMatch(exec ->
                        (exec.getStatus() == com.flowtex.Tracking.Domain.Model.ValueObjects.StepExecutionStatus.PENDING ||
                         exec.getStatus() == com.flowtex.Tracking.Domain.Model.ValueObjects.StepExecutionStatus.IN_PROGRESS)
                        && userId.equals(exec.getAssignedUserId())))
                .sorted(Comparator.comparing(Submission::getSubmittedAt).reversed())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Submission> getAll() {
        return submissionRepository.findAll().stream()
                .sorted(Comparator.comparing(Submission::getSubmittedAt).reversed())
                .toList();
    }
}
