-- FLOWTEX initial schema: IAM + FormBuilder bounded contexts.

CREATE TABLE roles (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    username        VARCHAR(80)   NOT NULL UNIQUE,
    email           VARCHAR(160)  NOT NULL UNIQUE,
    full_name       VARCHAR(160)  NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE forms (
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    title           VARCHAR(160)   NOT NULL,
    description     VARCHAR(500),
    context         VARCHAR(500),
    status          VARCHAR(20)    NOT NULL DEFAULT 'DRAFT',
    version         INT            NOT NULL DEFAULT 1,
    owner_id        BIGINT         NOT NULL,
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_forms_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE form_fields (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    form_id         BIGINT        NOT NULL,
    label           VARCHAR(160)  NOT NULL,
    field_key       VARCHAR(80)   NOT NULL,
    field_type      VARCHAR(30)   NOT NULL,
    required        BOOLEAN       NOT NULL DEFAULT FALSE,
    placeholder     VARCHAR(160),
    help_text       VARCHAR(255),
    position        INT           NOT NULL DEFAULT 0,
    options         TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_form_fields_form FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE
);

CREATE INDEX idx_forms_owner ON forms(owner_id);
CREATE INDEX idx_form_fields_form ON form_fields(form_id);
