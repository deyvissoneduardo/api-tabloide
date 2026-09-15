package com.tabloide.api.modules.conteudogeral.interfaces.http.dto;

import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarConteudoGeralRequest(
        @NotNull TipoConteudoGeral tipo,
        @NotBlank @Size(max = 200) String titulo,
        @NotBlank @Size(max = 20000) String corpo
) {
}
