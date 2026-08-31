package com.tabloide.api.modules.operation.interfaces.http.dto;

import com.tabloide.api.modules.operation.domain.SeveridadeOcorrencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AbrirOcorrenciaRequest(
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotNull SeveridadeOcorrencia severidade,
        Long supermercadoId,
        Long responsavelId
) {
}
