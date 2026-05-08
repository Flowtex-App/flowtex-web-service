package com.flowtex.Workflow.Domain.Model.ValueObjects;

public enum ApproverKind {
    /** Aprobador específico por id de usuario. */
    USER,
    /** Aprobador resuelto en runtime: cualquier usuario con (área, cargo) que matchee. */
    AREA_POSITION,
    /** Aprobador legacy por rol. Se mantiene por compat. */
    ROLE
}
