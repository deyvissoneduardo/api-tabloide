package com.tabloide.api.modules.analytics.interfaces.http.dto;

import com.tabloide.api.modules.analytics.application.UtilizacaoSupermercado;

public record UtilizacaoSupermercadoResponse(
        Long supermercadoId,
        String nomeFantasia,
        long quantidadeEventosPeriodoAtual,
        long quantidadeEventosPeriodoAnterior,
        Double variacaoPercentual
) {

    public static UtilizacaoSupermercadoResponse from(UtilizacaoSupermercado utilizacao) {
        return new UtilizacaoSupermercadoResponse(
                utilizacao.supermercadoId(),
                utilizacao.nomeFantasia(),
                utilizacao.quantidadeEventosPeriodoAtual(),
                utilizacao.quantidadeEventosPeriodoAnterior(),
                utilizacao.variacaoPercentual()
        );
    }
}
