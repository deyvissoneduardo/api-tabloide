package com.tabloide.api.modules.loja.interfaces.http.dto;

import jakarta.validation.constraints.NotBlank;

public record CadastrarLojaRequest(
        @NotBlank String nome,
        @NotBlank String cep,
        @NotBlank String logradouro,
        @NotBlank String numero,
        @NotBlank String bairro,
        @NotBlank String municipio,
        @NotBlank String uf,
        String complemento
) {
}
