package com.tabloide.api.modules.tabloide.application;

import com.tabloide.api.modules.tabloide.domain.TipoArquivoTabloide;
import java.time.Instant;
import java.util.Set;

public record DadosTabloide(
        String titulo,
        TipoArquivoTabloide tipoArquivo,
        String arquivoPdfUrl,
        Long arquivoPdfTamanhoBytes,
        Set<Long> lojaIds,
        Instant inicio,
        Instant fim
) {
}
