-- V4: Workflow bounded context — replaces NINTEX-style approval flows.
-- Each form may be linked to one workflow. A workflow is composed of ordered
-- steps; each step belongs to a role and may inject extra sections (a curated
-- set of system fields) when that step becomes active.

CREATE TABLE workflows (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    name         VARCHAR(160)  NOT NULL,
    description  VARCHAR(500),
    status       VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    owner_id     BIGINT        NOT NULL,
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_workflows_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE workflow_steps (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    workflow_id     BIGINT       NOT NULL,
    position        INT          NOT NULL,
    label           VARCHAR(160) NOT NULL,
    role            VARCHAR(80)  NOT NULL,
    sla_hours       INT          NOT NULL DEFAULT 48,
    -- approval mode: SEQUENTIAL | PARALLEL | MAJORITY
    mode            VARCHAR(20)  NOT NULL DEFAULT 'SEQUENTIAL',
    description     VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT fk_workflow_steps_workflow FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE
);

-- Sections injected into the form when a given step is active.
-- section_kind identifies the shape of the injected widget; client decides UI.
CREATE TABLE workflow_step_sections (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    step_id         BIGINT       NOT NULL,
    position        INT          NOT NULL,
    section_kind    VARCHAR(40)  NOT NULL,
    label           VARCHAR(160) NOT NULL,
    required        BOOLEAN      NOT NULL DEFAULT FALSE,
    config          TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_workflow_step_sections_step FOREIGN KEY (step_id) REFERENCES workflow_steps(id) ON DELETE CASCADE
);

-- A form is optionally linked to a workflow.
ALTER TABLE forms ADD COLUMN workflow_id BIGINT NULL;
ALTER TABLE forms ADD CONSTRAINT fk_forms_workflow FOREIGN KEY (workflow_id) REFERENCES workflows(id);

CREATE INDEX idx_workflow_steps_workflow ON workflow_steps(workflow_id);
CREATE INDEX idx_workflow_step_sections_step ON workflow_step_sections(step_id);
CREATE INDEX idx_forms_workflow ON forms(workflow_id);

-- Seed: one workflow shared by the example forms (sequential 2-level approval).
INSERT INTO workflows (id, name, description, status, owner_id, created_at, updated_at) VALUES
    (1, 'Aprobacion estandar TI',
        'Flujo secuencial de dos niveles para solicitudes operativas',
        'PUBLISHED', 1, '2026-04-05 10:00:00', '2026-04-05 10:00:00');

INSERT INTO workflow_steps (id, workflow_id, position, label, role, sla_hours, mode, description) VALUES
    (1, 1, 0, 'Revision TI',         'ROLE_APPROVER', 48, 'SEQUENTIAL', 'Validacion tecnica del area solicitante'),
    (2, 1, 1, 'Aprobacion gerencia', 'ROLE_ADMIN',    72, 'SEQUENTIAL', 'Aprobacion final del responsable de gerencia');

INSERT INTO workflow_step_sections (step_id, position, section_kind, label, required, config) VALUES
    (1, 0, 'COMMENTS',  'Comentarios del aprobador TI',     FALSE, NULL),
    (1, 1, 'DECISION',  'Decision',                         TRUE,  '{"options":["APROBAR","RECHAZAR","DEVOLVER"]}'),
    (2, 0, 'COMMENTS',  'Comentarios de gerencia',          FALSE, NULL),
    (2, 1, 'DECISION',  'Decision final',                   TRUE,  '{"options":["APROBAR","RECHAZAR"]}');

-- Link the seeded forms to the seeded workflow.
UPDATE forms SET workflow_id = 1 WHERE id IN (1, 2, 3, 4, 6);
