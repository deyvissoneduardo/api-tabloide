CREATE TABLE avisos_internos (
    id BIGSERIAL PRIMARY KEY,
    assinatura_id BIGINT NOT NULL REFERENCES assinaturas(id),
    destinatario_usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    tipo VARCHAR(40) NOT NULL,
    mensagem TEXT NOT NULL,
    lido_em TIMESTAMPTZ NULL,
    criado_em TIMESTAMPTZ NOT NULL,
    UNIQUE (assinatura_id, tipo)
);

CREATE INDEX idx_avisos_internos_destinatario ON avisos_internos (destinatario_usuario_id);
