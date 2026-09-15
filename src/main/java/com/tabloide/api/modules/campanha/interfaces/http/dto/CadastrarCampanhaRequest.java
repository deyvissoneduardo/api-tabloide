package com.tabloide.api.modules.campanha.interfaces.http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;

public record CadastrarCampanhaRequest(
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 1000) String descricao,
        @NotEmpty Set<@NotNull Long> lojaIds,
        Set<@NotNull Long> ofertaIds,
        @NotNull Instant inicio,
        @NotNull Instant fim,
        boolean confirmarPublicacao
) {
}
