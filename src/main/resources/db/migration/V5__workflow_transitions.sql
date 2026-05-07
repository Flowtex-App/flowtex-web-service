-- V5: Workflow becomes a graph instead of a linear list.
-- Each step has explicit outgoing transitions and a position on the canvas
-- (so the visual layout the designer arranged is preserved).

-- Visual position on the canvas (used by React Flow on the front-end).
ALTER TABLE workflow_steps ADD COLUMN canvas_x INT NOT NULL DEFAULT 0;
ALTER TABLE workflow_steps ADD COLUMN canvas_y INT NOT NULL DEFAULT 0;

-- Outgoing transitions from a step. A NULL to_step_id means the path
-- terminates the workflow (end node in the canvas).
CREATE TABLE workflow_step_transitions (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    from_step_id    BIGINT       NOT NULL,
    to_step_id      BIGINT       NULL,
    -- ALWAYS · ON_APPROVE · ON_REJECT · ON_RETURN · CUSTOM
    condition_kind  VARCHAR(40)  NOT NULL DEFAULT 'ALWAYS',
    label           VARCHAR(160),
    position        INT          NOT NULL DEFAULT 0,
    config          TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_wst_from FOREIGN KEY (from_step_id) REFERENCES workflow_steps(id) ON DELETE CASCADE,
    CONSTRAINT fk_wst_to   FOREIGN KEY (to_step_id)   REFERENCES workflow_steps(id) ON DELETE SET NULL
);

CREATE INDEX idx_wst_from ON workflow_step_transitions(from_step_id);
CREATE INDEX idx_wst_to ON workflow_step_transitions(to_step_id);

-- Seed: position the existing seeded steps in a left-to-right canvas layout
-- and create explicit transitions: step1 → step2 (ALWAYS), step2 → END (ALWAYS).
UPDATE workflow_steps SET canvas_x = 240, canvas_y = 160 WHERE id = 1;
UPDATE workflow_steps SET canvas_x = 560, canvas_y = 160 WHERE id = 2;

INSERT INTO workflow_step_transitions (from_step_id, to_step_id, condition_kind, label, position, config) VALUES
    (1, 2,    'ON_APPROVE', 'Aprobado por TI',    0, NULL),
    (1, NULL, 'ON_REJECT',  'Rechazado por TI',   1, NULL),
    (2, NULL, 'ON_APPROVE', 'Aprobacion final',   0, NULL),
    (2, NULL, 'ON_REJECT',  'Rechazo gerencia',   1, NULL);
