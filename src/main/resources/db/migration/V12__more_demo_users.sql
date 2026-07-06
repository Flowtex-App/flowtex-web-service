-- V12: usuarios demo adicionales para poblar el selector de aprobadores.
-- Nombres inventados, repartidos por area, cargo y rol para tener aprobadores
-- alcanzables en varias combinaciones. Todos comparten la contrasena de demo
-- "Flowtex2026!" (mismo hash BCrypt que el resto del seed).

INSERT INTO users (id, username, email, full_name, password_hash, created_at, employee_code, position, position_specialty, area) VALUES
    (7,  'aquispe',  'ana.quispe@claro.com.pe',      'Ana Quispe',      '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:00:00', 'C00007', 'JEFE',       'de Finanzas',              'FINANZAS'),
    (8,  'lvargas',  'luis.vargas@claro.com.pe',     'Luis Vargas',     '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:05:00', 'C00008', 'GERENTE',    'de Finanzas',              'FINANZAS'),
    (9,  'rflores',  'rosa.flores@claro.com.pe',     'Rosa Flores',     '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:10:00', 'C00009', 'JEFE',       'de Recursos Humanos',      'RECURSOS_HUMANOS'),
    (10, 'pramos',   'pedro.ramos@claro.com.pe',     'Pedro Ramos',     '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:15:00', 'C00010', 'ANALISTA',   'Legal',                    'LEGAL'),
    (11, 'cnunez',   'carla.nunez@claro.com.pe',     'Carla Nunez',     '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:20:00', 'C00011', 'GERENTE',    'Legal',                    'LEGAL'),
    (12, 'jsalazar', 'jorge.salazar@claro.com.pe',   'Jorge Salazar',   '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:25:00', 'C00012', 'SUBDIRECTOR','de Operaciones',           'OPERACIONES'),
    (13, 'mcastro',  'maria.castro@claro.com.pe',    'Maria Castro',    '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:30:00', 'C00013', 'GERENTE',    'de Mercado Corporativo',   'MERCADO_CORPORATIVO'),
    (14, 'dtorres',  'diego.torres@hitss.com',       'Diego Torres',    '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:35:00', 'C00014', 'JEFE',       'de Arquitectura',          'TECNOLOGIA'),
    (15, 'smendoza', 'sofia.mendoza@claro.com.pe',   'Sofia Mendoza',   '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:40:00', 'C00015', 'ANALISTA',   'de Compras',               'COMPRAS'),
    (16, 'erios',    'elena.rios@claro.com.pe',      'Elena Rios',      '$2a$10$kVCsRcxSP19vb77IDRUnL.vBy/c1l/C0LmOXZaw/YvwnnH8LZDMny', '2026-04-02 09:45:00', 'C00016', 'DIRECTOR',   'de Auditoria',             'AUDITORIA');

-- Roles: 1 ADMIN, 2 DESIGNER, 3 APPROVER, 4 USER
INSERT INTO user_roles (user_id, role_id) VALUES
    (7, 3), (7, 4),
    (8, 3),
    (9, 3), (9, 4),
    (10, 4),
    (11, 3),
    (12, 3),
    (13, 3),
    (14, 3), (14, 2),
    (15, 4),
    (16, 3), (16, 1);
