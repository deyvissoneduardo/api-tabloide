CREATE TABLE assinaturas (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados (id),
    plano_id BIGINT NOT NULL REFERENCES planos (id),
    plano_nome VARCHAR(255) NOT NULL,
    plano_validade_dias INTEGER NOT NULL,
    plano_valor NUMERIC(10, 2) NOT NULL,
    plano_limite_fotos INTEGER NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('AGENDADA', 'VIGENTE', 'VENCIDA', 'SUBSTITUIDA')),
    data_inicio TIMESTAMPTZ NOT NULL,
    data_fim TIMESTAMPTZ NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_assinaturas_supermercado ON assinaturas (supermercado_id);

-- Garante no máximo uma assinatura ativa (AGENDADA ou VIGENTE) por supermercado.
CREATE UNIQUE INDEX idx_assinaturas_ativa_unica ON assinaturas (supermercado_id) WHERE estado IN ('AGENDADA', 'VIGENTE');
