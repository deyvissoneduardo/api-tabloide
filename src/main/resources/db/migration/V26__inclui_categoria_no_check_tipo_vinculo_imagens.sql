-- A V17 criou o CHECK de tipo_vinculo sem 'CATEGORIA', embora o enum Java
-- TipoVinculoImagem sempre tenha incluído esse valor. Um INSERT com tipoVinculo=CATEGORIA
-- quebrava essa constraint.
ALTER TABLE imagens DROP CONSTRAINT imagens_tipo_vinculo_check;

ALTER TABLE imagens
    ADD CONSTRAINT imagens_tipo_vinculo_check CHECK (tipo_vinculo IN ('PRODUTO', 'CATEGORIA', 'BANNER', 'LOGOMARCA', 'TABLOIDE'));
