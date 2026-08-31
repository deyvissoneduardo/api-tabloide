package com.tabloide.api.modules.plano.application;

import java.math.BigDecimal;

public record DadosPlano(
        String nome,
        int validadeDias,
        BigDecimal valor,
        Integer limiteFotos,
        Integer limiteLojas
) {
}
