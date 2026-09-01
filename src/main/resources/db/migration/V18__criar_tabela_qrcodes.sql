CREATE TABLE qrcodes (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    loja_id BIGINT NOT NULL REFERENCES lojas(id),
    nome VARCHAR(255) NOT NULL,
    nome_normalizado VARCHAR(255) NOT NULL,
    codigo_publico VARCHAR(64) NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ATIVO', 'DESATIVADO')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (supermercado_id, nome_normalizado),
    UNIQUE (codigo_publico)
);

CREATE INDEX idx_qrcodes_loja ON qrcodes (loja_id);
