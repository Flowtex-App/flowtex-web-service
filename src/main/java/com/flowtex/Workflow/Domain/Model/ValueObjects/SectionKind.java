package com.flowtex.Workflow.Domain.Model.ValueObjects;

/**
 * The shape of an injected section that appears below the form when a given
 * approval step is active. Each kind is a fixed widget the front-end knows
 * how to render.
 */
public enum SectionKind {
    COMMENTS,
    DECISION,
    EVIDENCE,
    CHECKLIST,
    SLA_TIMER
}
