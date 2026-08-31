ALTER TABLE planos DROP CONSTRAINT planos_nome_key;

ALTER TABLE planos ADD COLUMN nome_normalizado VARCHAR(255);
UPDATE planos SET nome_normalizado = lower(trim(nome));
ALTER TABLE planos ALTER COLUMN nome_normalizado SET NOT NULL;

ALTER TABLE planos ADD COLUMN limite_lojas INTEGER NULL;
ALTER TABLE planos ADD COLUMN versao BIGINT NOT NULL DEFAULT 0;
ALTER TABLE planos ADD COLUMN criado_em TIMESTAMPTZ NOT NULL DEFAULT now();
ALTER TABLE planos ADD COLUMN atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now();
ALTER TABLE planos ADD COLUMN excluido_em TIMESTAMPTZ NULL;

CREATE UNIQUE INDEX idx_planos_nome_normalizado_ativo ON planos (nome_normalizado) WHERE excluido_em IS NULL;
