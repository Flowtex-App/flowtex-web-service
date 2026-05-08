-- V9: coordenadas explícitas de grid (col, row) y tipos auto/spacer.
-- col_start y row_start son nullable: cuando son null, el field fluye natural en el grid.
-- Cuando ambos están seteados, el field se posiciona en (col_start, row_start) absoluto.

ALTER TABLE form_fields ADD COLUMN col_start INT NULL;
ALTER TABLE form_fields ADD COLUMN row_start INT NULL;
ALTER TABLE form_fields ADD COLUMN row_span  INT NOT NULL DEFAULT 1;
