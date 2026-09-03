package com.tabloide.api.modules.produto.interfaces.http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CadastrarProdutoRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotEmpty Set<Long> categoriaIds,
        @Size(max = 150) String marca,
        @Size(max = 1000) String descricao,
        @Size(max = 50) String peso,
        @Size(max = 50) String unidade,
        @Size(max = 50) String volume
) {
}
