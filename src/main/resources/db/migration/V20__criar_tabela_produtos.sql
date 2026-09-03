CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    nome VARCHAR(150) NOT NULL,
    marca VARCHAR(150) NULL,
    descricao VARCHAR(1000) NULL,
    peso VARCHAR(50) NULL,
    unidade VARCHAR(50) NULL,
    volume VARCHAR(50) NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ATIVO', 'DESATIVADO')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_produtos_supermercado ON produtos (supermercado_id);

CREATE TABLE produto_categorias (
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    categoria_id BIGINT NOT NULL REFERENCES categorias(id),
    PRIMARY KEY (produto_id, categoria_id)
);
