CREATE TABLE campanhas (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(1000) NULL,
    inicio TIMESTAMPTZ NOT NULL,
    fim TIMESTAMPTZ NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('RASCUNHO', 'AGENDADA', 'VIGENTE', 'CANCELADA', 'EXPIRADA')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_campanhas_supermercado ON campanhas (supermercado_id);

CREATE TABLE campanha_lojas (
    campanha_id BIGINT NOT NULL REFERENCES campanhas(id),
    loja_id BIGINT NOT NULL REFERENCES lojas(id),
    PRIMARY KEY (campanha_id, loja_id)
);

CREATE TABLE campanha_ofertas (
    campanha_id BIGINT NOT NULL REFERENCES campanhas(id),
    oferta_id BIGINT NOT NULL REFERENCES ofertas(id),
    PRIMARY KEY (campanha_id, oferta_id)
);

CREATE INDEX idx_campanha_ofertas_oferta ON campanha_ofertas (oferta_id);
