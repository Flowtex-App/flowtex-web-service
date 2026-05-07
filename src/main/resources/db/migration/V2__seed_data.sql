-- Seed data for FLOWTEX. Passwords are BCrypt hashes of "Flowtex2026!"
-- BCrypt hash generated with strength 10: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO roles (id, name) VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_DESIGNER'),
    (3, 'ROLE_APPROVER'),
    (4, 'ROLE_USER');

INSERT INTO users (id, username, email, full_name, password_hash, created_at) VALUES
    (1, 'gmora',     'gabriel.mora@claro.com.pe',      'Gabriel Mora',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '2026-04-01 09:00:00'),
    (2, 'mtongo',    'milagros.tongo@hitss.com',       'Milagros Tongo',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '2026-04-01 09:05:00'),
    (3, 'clecca',    'christopher.lecca@hitss.com',    'Christopher Lecca',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '2026-04-01 09:10:00'),
    (4, 'mames',     'mariano.ames@hitss.com',         'Mariano Ames',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '2026-04-01 09:15:00'),
    (5, 'asosa',     'angello.sosa@hitss.com',         'Angello Sosa',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '2026-04-01 09:20:00'),
    (6, 'demo',      'demo@flowtex.app',               'Demo User',            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '2026-04-01 09:25:00');

INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1), (1, 2),
    (2, 2), (2, 3),
    (3, 1), (3, 2),
    (4, 4),
    (5, 4),
    (6, 1), (6, 2), (6, 3), (6, 4);

INSERT INTO forms (id, title, description, context, status, version, owner_id, created_at, updated_at) VALUES
    (1, 'Solicitud de Acceso a Sistemas',
        'Formulario para solicitar acceso a sistemas internos de Claro Peru',
        'Solicitud interna de TI para empleados nuevos que requieren acceso a Oracle, SQL Server, Active Directory y aplicaciones internas',
        'PUBLISHED', 3, 1, '2026-04-05 10:00:00', '2026-04-15 14:30:00'),
    (2, 'Aprobacion de Compras Operativas',
        'Solicitudes de compra de bienes y servicios operativos',
        'Compras operativas del area de Tecnologia con flujo de aprobacion por monto, gerencia y finanzas',
        'PUBLISHED', 5, 2, '2026-04-08 11:00:00', '2026-04-18 16:00:00'),
    (3, 'Reporte de Incidente de Seguridad',
        'Captura de incidentes de seguridad de la informacion',
        'Reporte de incidentes ISO 27001 - phishing, accesos no autorizados, perdida de datos, vulnerabilidades detectadas',
        'PUBLISHED', 2, 3, '2026-04-10 09:30:00', '2026-04-12 10:15:00'),
    (4, 'Solicitud de Vacaciones',
        'Gestion de solicitudes de vacaciones y permisos',
        'Workflow de RRHH con aprobacion del jefe directo y validacion de saldo de dias disponibles',
        'PUBLISHED', 4, 1, '2026-04-12 08:45:00', '2026-04-20 09:00:00'),
    (5, 'Onboarding Tecnico de Proveedores',
        'Alta de nuevos proveedores tecnologicos',
        'Validacion de proveedores externos: documentos legales, certificaciones ISO, referencias tecnicas y SLA propuesto',
        'DRAFT', 1, 3, '2026-04-22 15:00:00', '2026-04-22 15:00:00'),
    (6, 'Solicitud de Cambio en Produccion',
        'Change request para despliegues en produccion',
        'Gestion de cambios CMMI: descripcion del cambio, impacto, ventana, plan de rollback y aprobaciones del CAB',
        'PUBLISHED', 7, 5, '2026-04-25 12:00:00', '2026-05-01 10:30:00');

INSERT INTO form_fields (form_id, label, field_key, field_type, required, placeholder, help_text, position, options) VALUES
    (1, 'Nombre completo del solicitante', 'full_name', 'TEXT', TRUE, 'Ej. Gabriel Mora', NULL, 1, NULL),
    (1, 'Correo corporativo', 'corporate_email', 'EMAIL', TRUE, 'usuario@claro.com.pe', NULL, 2, NULL),
    (1, 'Sistemas requeridos', 'required_systems', 'MULTI_SELECT', TRUE, NULL, 'Selecciona uno o mas sistemas', 3, '["Oracle ERP","SQL Server","SharePoint","Active Directory","Jira","GitLab"]'),
    (1, 'Justificacion', 'justification', 'TEXTAREA', TRUE, 'Explica brevemente la necesidad', NULL, 4, NULL),
    (1, 'Fecha requerida', 'required_date', 'DATE', TRUE, NULL, NULL, 5, NULL),

    (2, 'Concepto de la compra', 'purchase_concept', 'TEXT', TRUE, 'Ej. Licencias SonarQube', NULL, 1, NULL),
    (2, 'Monto estimado (USD)', 'estimated_amount', 'NUMBER', TRUE, '0.00', NULL, 2, NULL),
    (2, 'Centro de costos', 'cost_center', 'SELECT', TRUE, NULL, NULL, 3, '["TI-Operaciones","TI-Desarrollo","TI-Seguridad","TI-Infraestructura"]'),
    (2, 'Adjuntar cotizacion', 'quotation_file', 'FILE', TRUE, NULL, 'PDF hasta 10MB', 4, NULL),
    (2, 'Urgencia', 'urgency', 'RADIO', TRUE, NULL, NULL, 5, '["Baja","Media","Alta","Critica"]'),

    (3, 'Tipo de incidente', 'incident_type', 'SELECT', TRUE, NULL, NULL, 1, '["Phishing","Acceso no autorizado","Perdida de dispositivo","Filtracion de datos","Malware"]'),
    (3, 'Fecha y hora del incidente', 'incident_datetime', 'DATETIME', TRUE, NULL, NULL, 2, NULL),
    (3, 'Descripcion detallada', 'description', 'TEXTAREA', TRUE, NULL, 'Que paso, cuando y como lo descubriste', 3, NULL),
    (3, 'Sistemas afectados', 'affected_systems', 'TEXT', FALSE, NULL, NULL, 4, NULL),
    (3, 'Evidencias', 'evidence', 'FILE', FALSE, NULL, 'Capturas, logs, correos', 5, NULL),

    (4, 'Fecha de inicio', 'start_date', 'DATE', TRUE, NULL, NULL, 1, NULL),
    (4, 'Fecha de fin', 'end_date', 'DATE', TRUE, NULL, NULL, 2, NULL),
    (4, 'Tipo de descanso', 'leave_type', 'SELECT', TRUE, NULL, NULL, 3, '["Vacaciones","Permiso por enfermedad","Permiso personal","Licencia sin goce"]'),
    (4, 'Persona de respaldo', 'backup_person', 'TEXT', TRUE, 'Quien atendera tus pendientes', NULL, 4, NULL),

    (5, 'Razon social del proveedor', 'company_name', 'TEXT', TRUE, NULL, NULL, 1, NULL),
    (5, 'RUC', 'tax_id', 'TEXT', TRUE, '20XXXXXXXXX', NULL, 2, NULL),
    (5, 'Certificaciones ISO', 'certifications', 'MULTI_SELECT', FALSE, NULL, NULL, 3, '["ISO 9001","ISO 27001","ISO 20000","ISO 14001"]'),
    (5, 'Referencias comerciales', 'references', 'TEXTAREA', FALSE, NULL, NULL, 4, NULL),

    (6, 'Titulo del cambio', 'change_title', 'TEXT', TRUE, NULL, NULL, 1, NULL),
    (6, 'Tipo de cambio', 'change_type', 'SELECT', TRUE, NULL, NULL, 2, '["Estandar","Normal","Emergencia"]'),
    (6, 'Descripcion del cambio', 'change_description', 'TEXTAREA', TRUE, NULL, NULL, 3, NULL),
    (6, 'Ventana de despliegue', 'deployment_window', 'DATETIME', TRUE, NULL, NULL, 4, NULL),
    (6, 'Plan de rollback', 'rollback_plan', 'TEXTAREA', TRUE, NULL, 'Que hacer si falla', 5, NULL),
    (6, 'Sistemas impactados', 'impacted_systems', 'MULTI_SELECT', TRUE, NULL, NULL, 6, '["FormBuilder","FlowEngine","MigraFlow","Active Directory","Microsoft Teams","Oracle ERP"]');
