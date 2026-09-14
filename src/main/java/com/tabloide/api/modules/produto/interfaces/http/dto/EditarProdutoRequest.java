package com.tabloide.api.modules.produto.interfaces.http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EditarProdutoRequest(
        @NotNull Long versao,
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 150) String marca,
        @Size(max = 1000) String descricao,
        @Size(max = 50) String peso,
        @Size(max = 50) String unidade,
        @Size(max = 50) String volume
) {
}
