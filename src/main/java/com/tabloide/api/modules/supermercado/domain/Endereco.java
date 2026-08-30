package com.tabloide.api.modules.supermercado.domain;

public record Endereco(
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String municipio,
        String uf
) {

    public Endereco {
        exigirPreenchido(cep, "CEP");
        exigirPreenchido(logradouro, "Logradouro");
        exigirPreenchido(numero, "Número");
        exigirPreenchido(bairro, "Bairro");
        exigirPreenchido(municipio, "Município");
        exigirPreenchido(uf, "UF");
    }

    private static void exigirPreenchido(String valor, String nomeCampo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nomeCampo + " é obrigatório");
        }
    }
}
