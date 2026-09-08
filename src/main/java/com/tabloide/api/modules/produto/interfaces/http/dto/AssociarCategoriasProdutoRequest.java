package com.tabloide.api.modules.produto.interfaces.http.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record AssociarCategoriasProdutoRequest(
        @NotNull Long versao,
        @NotEmpty Set<@NotNull Long> categoriaIds
) {
}
