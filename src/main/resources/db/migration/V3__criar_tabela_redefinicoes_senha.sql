CREATE TABLE redefinicoes_senha (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios (id),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    expira_em TIMESTAMPTZ NOT NULL,
    usado_em TIMESTAMPTZ NULL
);

CREATE INDEX idx_redefinicoes_senha_usuario_id ON redefinicoes_senha (usuario_id);
