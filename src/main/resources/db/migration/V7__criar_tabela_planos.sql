CREATE TABLE planos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    validade_dias INTEGER NOT NULL CHECK (validade_dias >= 7),
    valor NUMERIC(10, 2) NOT NULL CHECK (valor >= 0),
    limite_fotos INTEGER NULL
);
