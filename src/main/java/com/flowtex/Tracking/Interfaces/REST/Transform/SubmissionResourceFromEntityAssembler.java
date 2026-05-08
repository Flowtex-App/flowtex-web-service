package com.flowtex.Tracking.Interfaces.REST.Transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowtex.IAM.Domain.Repositories.UserRepository;
import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.Entities.SubmissionAuditEvent;
import com.flowtex.Tracking.Domain.Model.Entities.SubmissionStepExecution;
import com.flowtex.Tracking.Interfaces.REST.Resources.SubmissionAuditEventResource;
import com.flowtex.Tracking.Interfaces.REST.Resources.SubmissionResource;
import com.flowtex.Tracking.Interfaces.REST.Resources.SubmissionStepExecutionResource;

import java.util.Map;

public class SubmissionResourceFromEntityAssembler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static SubmissionResource toResource(Submission s, UserRepository userRepository) {
        return new SubmissionResource(
                s.getId(),
                s.getTicketCode(),
                s.getFormId(),
                s.getFormVersion(),
                s.getWorkflowId(),
                s.getSubmitterId(),
                userRepository == null ? null
                        : userRepository.findById(s.getSubmitterId())
                            .map(u -> u.getFullName()).orElse(null),
                s.getStatus().name(),
                parseMap(s.getDataJson()),
                parseMap(s.getFormSnapshot()),
                parseMap(s.getWorkflowSnapshot()),
                s.getCurrentStepRef(),
                s.getSubmittedAt(),
                s.getCompletedAt(),
                s.getOrderedExecutions().stream()
                        .map(SubmissionResourceFromEntityAssembler::toExec)
                        .toList(),
                s.getOrderedAudit().stream()
                        .map(SubmissionResourceFromEntityAssembler::toAudit)
                        .toList()
        );
    }

    private static SubmissionStepExecutionResource toExec(SubmissionStepExecution e) {
        return new SubmissionStepExecutionResource(
                e.getId(),
                e.getStepRef(),
                e.getStepLabel(),
                e.getPosition(),
                e.getAssignmentKind() != null ? e.getAssignmentKind().name() : null,
                e.getAssignedUserId(),
                e.getAssignedUserLabel(),
                e.getAssignedArea(),
                e.getAssignedPosition(),
                e.getAssignedRole(),
                e.getStatus().name(),
                e.getDecision() != null ? e.getDecision().name() : null,
                e.getComments(),
                e.getQueuedAt(),
                e.getStartedAt(),
                e.getCompletedAt(),
                e.getDecidedByUserId()
        );
    }

    private static SubmissionAuditEventResource toAudit(SubmissionAuditEvent a) {
        return new SubmissionAuditEventResource(
                a.getId(),
                a.getEventType().name(),
                a.getActorUserId(),
                a.getActorLabel(),
                a.getFieldKey(),
                a.getFieldLabel(),
                a.getOldValue(),
                a.getNewValue(),
                a.getDescription(),
                a.getTimestamp()
        );
    }

    private static Map<String, Object> parseMap(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
