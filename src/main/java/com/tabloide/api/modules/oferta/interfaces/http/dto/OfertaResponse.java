package com.tabloide.api.modules.oferta.interfaces.http.dto;

import com.tabloide.api.modules.oferta.domain.EstadoOferta;
import com.tabloide.api.modules.oferta.domain.Oferta;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record OfertaResponse(
        Long id,
        Long supermercadoId,
        Long produtoId,
        Set<Long> lojaIds,
        BigDecimal precoNormal,
        BigDecimal precoPromocional,
        Integer percentualDesconto,
        Instant inicio,
        Instant fim,
        String condicoes,
        EstadoOferta estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static OfertaResponse from(Oferta oferta) {
        return new OfertaResponse(
                oferta.id(),
                oferta.supermercadoId(),
                oferta.produtoId(),
                oferta.lojaIds(),
                oferta.precoNormal(),
                oferta.precoPromocional(),
                oferta.percentualDesconto(),
                oferta.inicio(),
                oferta.fim(),
                oferta.condicoes(),
                oferta.estado(),
                oferta.versao(),
                oferta.criadoEm(),
                oferta.atualizadoEm()
        );
    }
}
