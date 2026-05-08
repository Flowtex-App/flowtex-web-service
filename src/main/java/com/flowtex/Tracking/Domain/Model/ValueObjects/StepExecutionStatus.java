package com.flowtex.Tracking.Domain.Model.ValueObjects;

public enum StepExecutionStatus {
    PENDING,        // en cola, esperando aprobador
    IN_PROGRESS,    // un aprobador lo está revisando
    APPROVED,
    REJECTED,
    RETURNED,
    SKIPPED         // omitido por evaluación de transition
}
