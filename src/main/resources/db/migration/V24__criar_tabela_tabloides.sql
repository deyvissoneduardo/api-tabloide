CREATE TABLE tabloides (
    id BIGSERIAL PRIMARY KEY,
    supermercado_id BIGINT NOT NULL REFERENCES supermercados(id),
    titulo VARCHAR(255) NOT NULL,
    tipo_arquivo VARCHAR(10) NOT NULL CHECK (tipo_arquivo IN ('PDF', 'IMAGENS')),
    arquivo_pdf_url VARCHAR(500) NULL,
    arquivo_pdf_tamanho_bytes BIGINT NULL CHECK (arquivo_pdf_tamanho_bytes IS NULL OR (arquivo_pdf_tamanho_bytes > 0 AND arquivo_pdf_tamanho_bytes <= 20971520)),
    inicio TIMESTAMPTZ NOT NULL,
    fim TIMESTAMPTZ NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('RASCUNHO', 'AGENDADO', 'VIGENTE', 'DESATIVADO', 'EXPIRADO')),
    versao BIGINT NOT NULL DEFAULT 0,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tabloides_supermercado ON tabloides (supermercado_id);
CREATE INDEX idx_tabloides_supermercado_vigencia ON tabloides (supermercado_id, inicio, fim);

CREATE TABLE tabloide_lojas (
    tabloide_id BIGINT NOT NULL REFERENCES tabloides(id),
    loja_id BIGINT NOT NULL REFERENCES lojas(id),
    PRIMARY KEY (tabloide_id, loja_id)
);
