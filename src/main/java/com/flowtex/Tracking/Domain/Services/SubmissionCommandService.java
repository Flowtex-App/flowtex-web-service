package com.flowtex.Tracking.Domain.Services;

import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.Commands.CreateSubmissionCommand;
import com.flowtex.Tracking.Domain.Model.Commands.DecideStepCommand;
import com.flowtex.Tracking.Domain.Model.Commands.UpdateSubmissionDataCommand;

import java.util.Optional;

public interface SubmissionCommandService {
    Optional<Submission> handle(CreateSubmissionCommand command);
    Optional<Submission> handle(UpdateSubmissionDataCommand command);
    Optional<Submission> handle(DecideStepCommand command);
    void cancel(Long submissionId, Long actorUserId);
    /** Vuelve a enviar tras una devolución (pasa de RETURNED a IN_PROGRESS). */
    Optional<Submission> resubmit(Long submissionId, Long actorUserId);
}
