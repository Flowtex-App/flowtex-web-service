package com.flowtex.Tracking.Domain.Model.ValueObjects;

public enum SubmissionStatus {
    /** Borrador, todavía editable y no enviado a workflow. */
    DRAFT,
    /** Enviado y siendo procesado por el workflow. */
    IN_PROGRESS,
    /** Workflow terminó con resultado positivo. */
    APPROVED,
    /** Workflow terminó con resultado negativo. */
    REJECTED,
    /** Un aprobador devolvió la solicitud para corrección por el solicitante. */
    RETURNED,
    /** Cancelada por el solicitante o por un admin antes de completar. */
    CANCELED
}
