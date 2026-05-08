-- V11: bounded context Tracking — submissions, step executions y audit log.
-- Una submission "congela" el form y el workflow al momento del envío
-- (snapshots JSON), de modo que cambios futuros no afectan a las ya en curso.
--
-- IF NOT EXISTS y los nombres de columna sin colisión con keywords de TiDB
-- (last_value es función de ventana en TiDB; name suele dar parsing-edge cases)
-- garantizan que esta migración sea idempotente: si una corrida previa falló
-- a mitad de camino y dejó tablas a medias, la siguiente corrida no rompe.

CREATE TABLE IF NOT EXISTS submissions (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    ticket_code         VARCHAR(20)   NOT NULL UNIQUE,
    form_id             BIGINT        NOT NULL,
    form_version        INT           NOT NULL DEFAULT 1,
    workflow_id         BIGINT        NULL,
    form_snapshot       LONGTEXT      NOT NULL,
    workflow_snapshot   LONGTEXT      NULL,
    submitter_id        BIGINT        NOT NULL,
    status              VARCHAR(20)   NOT NULL DEFAULT 'IN_PROGRESS',
    data_json           LONGTEXT      NOT NULL,
    current_step_ref    VARCHAR(80)   NULL,
    submitted_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at        DATETIME      NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_submissions_form      FOREIGN KEY (form_id)      REFERENCES forms(id),
    CONSTRAINT fk_submissions_submitter FOREIGN KEY (submitter_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_submissions_submitter ON submissions(submitter_id);
CREATE INDEX IF NOT EXISTS idx_submissions_status    ON submissions(status);
CREATE INDEX IF NOT EXISTS idx_submissions_form      ON submissions(form_id);

-- Una fila por step ejecutado (o por ejecutar) en una submission.
CREATE TABLE IF NOT EXISTS submission_step_executions (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    submission_id       BIGINT        NOT NULL,
    step_ref            VARCHAR(80)   NOT NULL,
    step_label          VARCHAR(160)  NOT NULL,
    position            INT           NOT NULL,
    assignment_kind     VARCHAR(20)   NOT NULL,
    assigned_user_id    BIGINT        NULL,
    assigned_user_label VARCHAR(160)  NULL,
    assigned_area       VARCHAR(40)   NULL,
    assigned_position   VARCHAR(20)   NULL,
    assigned_role       VARCHAR(80)   NULL,
    status              VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    decision            VARCHAR(20)   NULL,
    comments            TEXT          NULL,
    queued_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at          DATETIME      NULL,
    completed_at        DATETIME      NULL,
    decided_by_user_id  BIGINT        NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sse_submission FOREIGN KEY (submission_id)      REFERENCES submissions(id) ON DELETE CASCADE,
    CONSTRAINT fk_sse_user       FOREIGN KEY (assigned_user_id)   REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_sse_decided    FOREIGN KEY (decided_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_sse_submission     ON submission_step_executions(submission_id);
CREATE INDEX IF NOT EXISTS idx_sse_assigned_user  ON submission_step_executions(assigned_user_id);
CREATE INDEX IF NOT EXISTS idx_sse_status         ON submission_step_executions(status);

-- Log inmutable de eventos sobre la submission (audit trail).
CREATE TABLE IF NOT EXISTS submission_audit_events (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    submission_id   BIGINT        NOT NULL,
    event_type      VARCHAR(40)   NOT NULL,
    actor_user_id   BIGINT        NULL,
    actor_label     VARCHAR(160)  NULL,
    field_key       VARCHAR(80)   NULL,
    field_label     VARCHAR(160)  NULL,
    old_value       TEXT          NULL,
    new_value       TEXT          NULL,
    description     VARCHAR(500)  NULL,
    data_json       TEXT          NULL,
    `timestamp`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_sae_submission FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
    CONSTRAINT fk_sae_actor      FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_sae_submission ON submission_audit_events(submission_id);
CREATE INDEX IF NOT EXISTS idx_sae_timestamp  ON submission_audit_events(`timestamp`);

-- Secuencia de tickets. Renombramos las columnas para evitar colisiones con
-- keywords contextuales de TiDB (`name` y `last_value` rompen el parser de
-- la versión Serverless actual).
CREATE TABLE IF NOT EXISTS ticket_sequence (
    seq_name   VARCHAR(40) NOT NULL,
    seq_value  BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (seq_name)
);

INSERT INTO ticket_sequence (seq_name, seq_value) VALUES ('FTX', 0)
    ON DUPLICATE KEY UPDATE seq_value = seq_value;
