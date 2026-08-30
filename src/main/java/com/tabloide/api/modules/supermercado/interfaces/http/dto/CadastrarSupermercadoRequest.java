package com.tabloide.api.modules.supermercado.interfaces.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastrarSupermercadoRequest(
        @NotBlank String cnpj,
        @NotBlank String razaoSocial,
        @NotBlank String nomeFantasia,
        @NotBlank @Email String emailComercial,
        @NotBlank String telefoneComercial,
        @NotBlank String cep,
        @NotBlank String logradouro,
        @NotBlank String numero,
        @NotBlank String bairro,
        @NotBlank String municipio,
        @NotBlank String uf,
        String complemento,
        String logomarcaUrl,
        String observacoesInternas
) {
}
