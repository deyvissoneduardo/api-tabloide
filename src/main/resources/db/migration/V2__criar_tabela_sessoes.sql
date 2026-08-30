CREATE TABLE sessoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios (id),
    jti VARCHAR(36) NOT NULL UNIQUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    expira_em TIMESTAMPTZ NOT NULL,
    ultimo_uso_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    revogada_em TIMESTAMPTZ NULL
);

CREATE INDEX idx_sessoes_usuario_id ON sessoes (usuario_id);
