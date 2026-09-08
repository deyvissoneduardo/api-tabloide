CREATE TABLE ofertas (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    preco_normal NUMERIC(10,2) NOT NULL,
    preco_promocional NUMERIC(10,2) NOT NULL,
    inicio TIMESTAMPTZ NOT NULL,
    fim TIMESTAMPTZ NOT NULL,
    condicoes VARCHAR(500) NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('RASCUNHO', 'AGENDADA', 'VIGENTE', 'DESATIVADA', 'CANCELADA', 'EXPIRADA')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_ofertas_supermercado ON ofertas (supermercado_id);

CREATE TABLE oferta_lojas (
    oferta_id BIGINT NOT NULL REFERENCES ofertas(id),
    loja_id BIGINT NOT NULL REFERENCES lojas(id),
    PRIMARY KEY (oferta_id, loja_id)
);
