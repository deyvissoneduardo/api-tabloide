package com.tabloide.api.modules.tabloide.interfaces.http.dto;

import com.tabloide.api.modules.tabloide.domain.TipoArquivoTabloide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

public record DisponibilizarTabloideRequest(
        @NotBlank String titulo,
        @NotNull TipoArquivoTabloide tipoArquivo,
        String arquivoPdfUrl,
        Long arquivoPdfTamanhoBytes,
        @NotEmpty Set<@NotNull Long> lojaIds,
        @NotNull Instant inicio,
        @NotNull Instant fim
) {
}
