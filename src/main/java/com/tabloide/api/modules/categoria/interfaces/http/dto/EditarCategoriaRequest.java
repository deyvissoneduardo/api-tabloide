package com.tabloide.api.modules.categoria.interfaces.http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditarCategoriaRequest(
        @NotNull Long versao,
        @NotBlank String nome,
        String descricao
) {
}
