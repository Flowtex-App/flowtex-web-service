-- V7: alinea seed con la marca del producto (solo Claro Perú).
-- Reemplaza emails @hitss.com de los usuarios sembrados y cualquier opción
-- residual de "MigraFlow" en los selects del seed inicial.

UPDATE users SET email = 'milagros.tongo@claro.com.pe'    WHERE username = 'mtongo';
UPDATE users SET email = 'christopher.lecca@claro.com.pe' WHERE username = 'clecca';
UPDATE users SET email = 'mariano.ames@claro.com.pe'      WHERE username = 'mames';
UPDATE users SET email = 'angello.sosa@claro.com.pe'      WHERE username = 'asosa';

UPDATE form_fields
SET options = '["FormBuilder","FlowEngine","Active Directory","Microsoft Teams","Oracle ERP","SQL Server"]'
WHERE field_key = 'impacted_systems';
