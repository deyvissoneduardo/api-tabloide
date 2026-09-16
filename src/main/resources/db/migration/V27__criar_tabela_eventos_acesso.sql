CREATE TABLE eventos_acesso (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('ACESSO_PAGINA', 'SCAN_QR_CODE', 'VISUALIZACAO_OFERTA')),
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    loja_id BIGINT NULL REFERENCES lojas(id),
    recurso_id BIGINT NULL,
    evento_tecnico_id VARCHAR(100) NULL,
    instante TIMESTAMPTZ NOT NULL,
    UNIQUE (evento_tecnico_id)
);

CREATE INDEX idx_eventos_acesso_supermercado_instante ON eventos_acesso (supermercado_id, instante);
