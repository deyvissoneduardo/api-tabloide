ALTER TABLE registros_auditoria ADD COLUMN supermercado_id BIGINT NULL;

CREATE INDEX idx_registros_auditoria_supermercado ON registros_auditoria (supermercado_id);
