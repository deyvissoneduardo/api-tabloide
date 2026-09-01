CREATE TABLE imagens (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    nome_busca VARCHAR(255) NOT NULL,
    tipo_vinculo VARCHAR(20) NOT NULL CHECK (tipo_vinculo IN ('PRODUTO', 'BANNER', 'LOGOMARCA', 'TABLOIDE')),
    vinculo_id BIGINT NULL,
    url_ou_chave VARCHAR(500) NOT NULL,
    formato VARCHAR(10) NOT NULL CHECK (formato IN ('JPG', 'PNG', 'WEBP')),
    tamanho_bytes BIGINT NOT NULL CHECK (tamanho_bytes > 0 AND tamanho_bytes <= 5242880),
    upload_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    excluido_em TIMESTAMPTZ NULL
);

CREATE INDEX idx_imagens_supermercado ON imagens (supermercado_id);
CREATE INDEX idx_imagens_nome_busca ON imagens (nome_busca);
