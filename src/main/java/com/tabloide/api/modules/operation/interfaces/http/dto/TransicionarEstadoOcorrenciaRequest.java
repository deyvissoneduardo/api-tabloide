package com.tabloide.api.modules.operation.interfaces.http.dto;

import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import jakarta.validation.constraints.NotNull;

public record TransicionarEstadoOcorrenciaRequest(
        @NotNull EstadoOcorrencia novoEstado,
        String comentario,
        @NotNull Long versao
) {
}
