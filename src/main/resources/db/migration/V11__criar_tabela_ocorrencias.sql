CREATE TABLE ocorrencias (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT NOT NULL,
    severidade VARCHAR(10) NOT NULL CHECK (severidade IN ('BAIXA', 'MEDIA', 'ALTA', 'CRITICA')),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ABERTA', 'EM_ANALISE', 'RESOLVIDA', 'ENCERRADA')),
    supermercado_id BIGINT NULL REFERENCES supermercados (id),
    responsavel_id BIGINT NULL REFERENCES usuarios (id),
    aberta_em TIMESTAMPTZ NOT NULL,
    resolvida_em TIMESTAMPTZ NULL,
    encerrada_em TIMESTAMPTZ NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    versao BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_ocorrencias_supermercado ON ocorrencias (supermercado_id);
CREATE INDEX idx_ocorrencias_estado ON ocorrencias (estado);
