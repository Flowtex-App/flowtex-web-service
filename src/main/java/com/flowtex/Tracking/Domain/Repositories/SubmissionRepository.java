package com.flowtex.Tracking.Domain.Repositories;

import com.flowtex.Tracking.Domain.Model.Aggregates.Submission;
import com.flowtex.Tracking.Domain.Model.ValueObjects.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByTicketCode(String ticketCode);
    List<Submission> findBySubmitterIdOrderBySubmittedAtDesc(Long submitterId);
    List<Submission> findByStatusOrderBySubmittedAtDesc(SubmissionStatus status);
}
