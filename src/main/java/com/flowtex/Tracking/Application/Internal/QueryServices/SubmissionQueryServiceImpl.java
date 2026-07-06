package com.flowtex.Tracking.Application.Internal.QueryServices;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Repositories.UserRepository;
import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.ValueObjects.AssignmentKind;
import com.flowtex.Tracking.Domain.Model.ValueObjects.StepExecutionStatus;
import com.flowtex.Tracking.Domain.Repositories.SubmissionRepository;
import com.flowtex.Tracking.Domain.Services.SubmissionQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SubmissionQueryServiceImpl implements SubmissionQueryService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public SubmissionQueryServiceImpl(SubmissionRepository submissionRepository,
                                      UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
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
        // Submissions con al menos un step PENDING/IN_PROGRESS que el usuario puede
        // decidir. La asignacion puede ser por USUARIO, por ROL o por AREA/CARGO;
        // antes solo se miraba assignedUserId, por lo que los pasos asignados por rol
        // (los sembrados) nunca aparecian en "Por aprobar".
        User user = userRepository.findById(userId).orElse(null);
        final Set<String> roleNames = user != null ? new HashSet<>(user.getRoleNames()) : Set.of();
        final String areaName = (user != null && user.getArea() != null) ? user.getArea().name() : null;
        final String positionName = (user != null && user.getPosition() != null) ? user.getPosition().name() : null;

        return submissionRepository.findAll().stream()
                .filter(s -> s.getOrderedExecutions().stream().anyMatch(exec -> {
                    boolean open = exec.getStatus() == StepExecutionStatus.PENDING
                            || exec.getStatus() == StepExecutionStatus.IN_PROGRESS;
                    if (!open) return false;

                    AssignmentKind kind = exec.getAssignmentKind();
                    if (kind == null) return userId.equals(exec.getAssignedUserId());

                    return switch (kind) {
                        case USER -> userId.equals(exec.getAssignedUserId());
                        case ROLE -> exec.getAssignedRole() != null
                                && roleNames.contains(exec.getAssignedRole());
                        case AREA_POSITION -> areaName != null && positionName != null
                                && areaName.equals(exec.getAssignedArea())
                                && positionName.equals(exec.getAssignedPosition());
                    };
                }))
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
