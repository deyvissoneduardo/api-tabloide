package com.tabloide.api.modules.categoria.interfaces.http.dto;

import jakarta.validation.constraints.NotBlank;

public record CadastrarCategoriaRequest(
        @NotBlank String nome,
        String descricao
) {
}
