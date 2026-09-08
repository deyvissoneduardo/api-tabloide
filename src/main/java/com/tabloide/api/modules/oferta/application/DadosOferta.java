package com.tabloide.api.modules.oferta.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record DadosOferta(
        Long produtoId,
        Set<Long> lojaIds,
        BigDecimal precoNormal,
        BigDecimal precoPromocional,
        Instant inicio,
        Instant fim,
        String condicoes,
        boolean confirmarPublicacao
) {
}
