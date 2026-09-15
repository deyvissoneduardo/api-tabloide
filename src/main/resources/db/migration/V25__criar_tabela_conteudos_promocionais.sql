CREATE TABLE conteudos_promocionais (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('MENSAGEM', 'AVISO', 'BANNER')),
    titulo VARCHAR(255) NOT NULL,
    texto VARCHAR(1000) NULL,
    nivel VARCHAR(15) NULL CHECK (nivel IS NULL OR nivel IN ('INFORMATIVO', 'ATENCAO', 'URGENTE')),
    destino VARCHAR(500) NULL,
    inicio TIMESTAMPTZ NOT NULL,
    fim TIMESTAMPTZ NOT NULL,
    posicao INTEGER NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('RASCUNHO', 'AGENDADO', 'VIGENTE', 'DESATIVADO', 'EXPIRADO')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_conteudos_promocionais_supermercado ON conteudos_promocionais (supermercado_id);

-- RN-007: nunca duas posições iguais no mesmo supermercado. Diferida para o commit porque
-- reordenar troca posições de várias linhas na mesma transação, passando por estados
-- intermediários com valores momentaneamente repetidos.
ALTER TABLE conteudos_promocionais
    ADD CONSTRAINT uk_conteudos_promocionais_posicao UNIQUE (supermercado_id, posicao) DEFERRABLE INITIALLY DEFERRED;

CREATE TABLE conteudo_promocional_lojas (
    conteudo_promocional_id BIGINT NOT NULL REFERENCES conteudos_promocionais(id),
    loja_id BIGINT NOT NULL REFERENCES lojas(id),
    PRIMARY KEY (conteudo_promocional_id, loja_id)
);
