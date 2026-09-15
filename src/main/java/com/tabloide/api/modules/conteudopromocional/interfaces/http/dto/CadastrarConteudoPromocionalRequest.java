package com.tabloide.api.modules.conteudopromocional.interfaces.http.dto;

import com.tabloide.api.modules.conteudopromocional.domain.NivelAviso;
import com.tabloide.api.modules.conteudopromocional.domain.TipoConteudoPromocional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;

public record CadastrarConteudoPromocionalRequest(
        @NotNull TipoConteudoPromocional tipo,
        @NotBlank String titulo,
        @Size(max = 1000) String texto,
        NivelAviso nivel,
        @Size(max = 500) String destino,
        @NotEmpty Set<@NotNull Long> lojaIds,
        @NotNull Instant inicio,
        @NotNull Instant fim
) {
}
