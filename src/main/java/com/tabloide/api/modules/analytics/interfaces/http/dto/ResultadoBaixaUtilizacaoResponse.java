package com.tabloide.api.modules.analytics.interfaces.http.dto;

import com.tabloide.api.modules.analytics.application.ResultadoBaixaUtilizacao;
import com.tabloide.api.modules.analytics.application.SupermercadoBaixaUtilizacao;
import java.util.List;

public record ResultadoBaixaUtilizacaoResponse(
        List<ItemResponse> itens,
        double medianaSupermercadosAtivos,
        double percentualLimiarBaixaUtilizacao
) {

    public static ResultadoBaixaUtilizacaoResponse from(ResultadoBaixaUtilizacao resultado) {
        return new ResultadoBaixaUtilizacaoResponse(
                resultado.itens().stream().map(ItemResponse::from).toList(),
                resultado.medianaSupermercadosAtivos(),
                resultado.percentualLimiarBaixaUtilizacao()
        );
    }

    public record ItemResponse(Long supermercadoId, String nomeFantasia, long quantidadeEventos, String classificacao) {

        public static ItemResponse from(SupermercadoBaixaUtilizacao item) {
            return new ItemResponse(item.supermercadoId(), item.nomeFantasia(), item.quantidadeEventos(), item.classificacao().name());
        }
    }
}
