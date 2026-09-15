package com.tabloide.api.modules.tabloide.interfaces.http.dto;

import com.tabloide.api.modules.tabloide.domain.EstadoTabloide;
import com.tabloide.api.modules.tabloide.domain.Tabloide;
import com.tabloide.api.modules.tabloide.domain.TipoArquivoTabloide;
import java.time.Instant;
import java.util.Set;

public record TabloideResponse(
        Long id,
        Long supermercadoId,
        String titulo,
        TipoArquivoTabloide tipoArquivo,
        String arquivoPdfUrl,
        Long arquivoPdfTamanhoBytes,
        Set<Long> lojaIds,
        Instant inicio,
        Instant fim,
        EstadoTabloide estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static TabloideResponse from(Tabloide tabloide) {
        Instant agora = Instant.now();
        return new TabloideResponse(
                tabloide.id(),
                tabloide.supermercadoId(),
                tabloide.titulo(),
                tabloide.tipoArquivo(),
                tabloide.arquivoPdfUrl(),
                tabloide.arquivoPdfTamanhoBytes(),
                tabloide.lojaIds(),
                tabloide.inicio(),
                tabloide.fim(),
                tabloide.estadoEfetivo(agora),
                tabloide.versao(),
                tabloide.criadoEm(),
                tabloide.atualizadoEm()
        );
    }
}
