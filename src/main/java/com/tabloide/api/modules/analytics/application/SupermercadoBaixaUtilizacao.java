package com.tabloide.api.modules.analytics.application;

import com.tabloide.api.modules.supermercado.domain.Supermercado;

public record SupermercadoBaixaUtilizacao(Long supermercadoId, String nomeFantasia, long quantidadeEventos, ClassificacaoUtilizacao classificacao) {

    static SupermercadoBaixaUtilizacao de(Supermercado supermercado, long quantidadeEventos, ClassificacaoUtilizacao classificacao) {
        return new SupermercadoBaixaUtilizacao(supermercado.id(), supermercado.nomeFantasia(), quantidadeEventos, classificacao);
    }
}
