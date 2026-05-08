-- V8: amplía usuario con metadata corporativa de Claro Perú.
-- Cada empleado se identifica unívocamente por su código (formato C12345)
-- y tiene un cargo + área que se usan para resolver aprobadores en workflows.

ALTER TABLE users ADD COLUMN employee_code      VARCHAR(20)  NULL;
ALTER TABLE users ADD COLUMN position           VARCHAR(20)  NULL;
ALTER TABLE users ADD COLUMN position_specialty VARCHAR(120) NULL;
ALTER TABLE users ADD COLUMN area               VARCHAR(40)  NULL;

UPDATE users SET employee_code='C00001', position='ANALISTA',  position_specialty='de Sistemas',  area='TECNOLOGIA' WHERE username='gmora';
UPDATE users SET employee_code='C00002', position='JEFE',      position_specialty='de Calidad',   area='TECNOLOGIA' WHERE username='mtongo';
UPDATE users SET employee_code='C00003', position='ANALISTA',  position_specialty='de Backend',   area='TECNOLOGIA' WHERE username='clecca';
UPDATE users SET employee_code='C00004', position='ANALISTA',  position_specialty='de QA',        area='TECNOLOGIA' WHERE username='mames';
UPDATE users SET employee_code='C00005', position='ANALISTA',  position_specialty='de Frontend',  area='TECNOLOGIA' WHERE username='asosa';
UPDATE users SET employee_code='C99999', position='GERENTE',   position_specialty='Demo Manager', area='TECNOLOGIA' WHERE username='demo';

ALTER TABLE users MODIFY COLUMN employee_code VARCHAR(20) NOT NULL;
ALTER TABLE users MODIFY COLUMN position      VARCHAR(20) NOT NULL;
ALTER TABLE users MODIFY COLUMN area          VARCHAR(40) NOT NULL;

CREATE UNIQUE INDEX uq_users_employee_code ON users(employee_code);
CREATE INDEX idx_users_area_position ON users(area, position);
