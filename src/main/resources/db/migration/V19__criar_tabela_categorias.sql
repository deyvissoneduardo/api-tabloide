CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(1000) NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ATIVA', 'DESATIVADA')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_categorias_supermercado ON categorias (supermercado_id);
