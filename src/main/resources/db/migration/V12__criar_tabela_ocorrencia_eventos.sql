CREATE TABLE ocorrencia_eventos (
    id BIGSERIAL PRIMARY KEY,
    ocorrencia_id BIGINT NOT NULL REFERENCES ocorrencias (id),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('COMENTARIO', 'TRANSICAO_ESTADO')),
    estado_anterior VARCHAR(20) NULL,
    estado_novo VARCHAR(20) NULL,
    comentario TEXT NULL,
    autor_id BIGINT NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_ocorrencia_eventos_ocorrencia ON ocorrencia_eventos (ocorrencia_id, criado_em);
