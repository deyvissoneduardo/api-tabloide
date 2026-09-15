CREATE TABLE conteudos_gerais (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('TERMOS_USO', 'POLITICA_PRIVACIDADE', 'CONTATO_SUPORTE', 'COMUNICADO_GERAL')),
    titulo VARCHAR(200) NOT NULL,
    corpo TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('RASCUNHO', 'PUBLICADO', 'ARQUIVADO')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    publicado_em TIMESTAMPTZ NULL
);

CREATE INDEX idx_conteudos_gerais_tipo ON conteudos_gerais (tipo);
