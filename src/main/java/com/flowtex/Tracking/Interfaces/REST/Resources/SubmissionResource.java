package com.flowtex.Tracking.Interfaces.REST.Resources;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record SubmissionResource(
        Long id,
        String ticketCode,
        Long formId,
        int formVersion,
        Long workflowId,
        Long submitterId,
        String submitterLabel,
        String status,
        Map<String, Object> data,
        Map<String, Object> formSnapshot,
        Map<String, Object> workflowSnapshot,
        String currentStepRef,
        LocalDateTime submittedAt,
        LocalDateTime completedAt,
        List<SubmissionStepExecutionResource> stepExecutions,
        List<SubmissionAuditEventResource> auditEvents
) {
}
