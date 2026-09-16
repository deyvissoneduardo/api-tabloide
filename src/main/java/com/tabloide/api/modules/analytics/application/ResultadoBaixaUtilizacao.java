package com.tabloide.api.modules.analytics.application;

import java.util.List;

// RN-013: o critério de baixa utilização/inatividade usado deve ser exibido na interface.
public record ResultadoBaixaUtilizacao(
        List<SupermercadoBaixaUtilizacao> itens,
        double medianaSupermercadosAtivos,
        double percentualLimiarBaixaUtilizacao
) {
}
