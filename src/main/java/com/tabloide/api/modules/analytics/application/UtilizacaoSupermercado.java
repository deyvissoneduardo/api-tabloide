package com.tabloide.api.modules.analytics.application;

import com.tabloide.api.modules.supermercado.domain.Supermercado;
import java.math.BigDecimal;
import java.math.RoundingMode;

// RN-010 (Analytics): comparações exibem valor absoluto e variação percentual; quando a base é
// zero, a variação não é calculável (variacaoPercentual nulo).
public record UtilizacaoSupermercado(
        Long supermercadoId,
        String nomeFantasia,
        long quantidadeEventosPeriodoAtual,
        long quantidadeEventosPeriodoAnterior,
        Double variacaoPercentual
) {

    static UtilizacaoSupermercado calcular(Supermercado supermercado, long quantidadeAtual, long quantidadeAnterior) {
        return new UtilizacaoSupermercado(
                supermercado.id(),
                supermercado.nomeFantasia(),
                quantidadeAtual,
                quantidadeAnterior,
                calcularVariacaoPercentual(quantidadeAtual, quantidadeAnterior)
        );
    }

    private static Double calcularVariacaoPercentual(long atual, long anterior) {
        if (anterior == 0) {
            return null;
        }
        return BigDecimal.valueOf(atual - anterior)
                .divide(BigDecimal.valueOf(anterior), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
