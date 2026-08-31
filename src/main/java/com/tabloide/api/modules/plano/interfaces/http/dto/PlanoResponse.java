package com.tabloide.api.modules.plano.interfaces.http.dto;

import com.tabloide.api.modules.plano.domain.Plano;
import java.math.BigDecimal;
import java.time.Instant;

public record PlanoResponse(
        Long id,
        String nome,
        int validadeDias,
        BigDecimal valor,
        Integer limiteFotos,
        Integer limiteLojas,
        Long versao,
        boolean excluido,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static PlanoResponse from(Plano plano) {
        return new PlanoResponse(
                plano.id(),
                plano.nome(),
                plano.validadeDias(),
                plano.valor(),
                plano.limiteFotos(),
                plano.limiteLojas(),
                plano.versao(),
                plano.estaExcluido(),
                plano.criadoEm(),
                plano.atualizadoEm()
        );
    }
}
