CREATE TABLE lojas (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    nome VARCHAR(255) NOT NULL,
    nome_normalizado VARCHAR(255) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    bairro VARCHAR(255) NOT NULL,
    municipio VARCHAR(255) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    complemento VARCHAR(255) NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ATIVA', 'DESATIVADA')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (supermercado_id, nome_normalizado)
);

CREATE INDEX idx_lojas_supermercado ON lojas (supermercado_id);
