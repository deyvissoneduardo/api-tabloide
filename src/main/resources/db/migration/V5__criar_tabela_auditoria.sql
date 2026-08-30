CREATE TABLE registros_auditoria (
    id BIGSERIAL PRIMARY KEY,
    ator_id BIGINT NULL,
    perfil_ator VARCHAR(20) NOT NULL CHECK (perfil_ator IN ('SUPER_ADMIN', 'DONO', 'OPERADOR')),
    acao VARCHAR(100) NOT NULL,
    entidade VARCHAR(100) NOT NULL,
    entidade_id BIGINT NOT NULL,
    dados_antes TEXT NULL,
    dados_depois TEXT NOT NULL,
    instante TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_registros_auditoria_entidade ON registros_auditoria (entidade, entidade_id);
