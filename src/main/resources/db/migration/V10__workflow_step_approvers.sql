-- V10: aprobadores explícitos por step.
-- Cada step guarda una lista de approvers que pueden ser:
--   USER          -> apuntan a un usuario concreto vía user_id
--   AREA_POSITION -> filtro dinámico por área + cargo (resuelto en runtime)
--   ROLE          -> aprobador legacy por rol (compat con seed previo)
-- El modo del step (SEQUENTIAL / PARALLEL / MAJORITY) sigue determinando si
-- todos deben aprobar o sólo la mayoría.

CREATE TABLE workflow_step_approvers (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    step_id      BIGINT       NOT NULL,
    position     INT          NOT NULL DEFAULT 0,
    kind         VARCHAR(20)  NOT NULL,
    user_id      BIGINT       NULL,
    area         VARCHAR(40)  NULL,
    user_position VARCHAR(20) NULL,
    role         VARCHAR(80)  NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wsa_step FOREIGN KEY (step_id) REFERENCES workflow_steps(id) ON DELETE CASCADE,
    CONSTRAINT fk_wsa_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_wsa_step ON workflow_step_approvers(step_id);
CREATE INDEX idx_wsa_user ON workflow_step_approvers(user_id);

-- Backfill desde el rol legacy: cada step seedeado pasa a tener un approver
-- de tipo ROLE con su rol actual, así no se pierde la semántica anterior.
INSERT INTO workflow_step_approvers (step_id, position, kind, role)
SELECT id, 0, 'ROLE', role FROM workflow_steps;
