CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL CHECK (perfil IN ('SUPER_ADMIN', 'DONO', 'OPERADOR')),
    supermercado_id BIGINT NULL,
    supermercado_cnpj VARCHAR(14) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    tentativas_login_invalidas INTEGER NOT NULL DEFAULT 0,
    bloqueado_ate TIMESTAMPTZ NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_usuarios_supermercado_cnpj ON usuarios (supermercado_cnpj);
