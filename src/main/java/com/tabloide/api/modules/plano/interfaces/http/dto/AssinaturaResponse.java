package com.tabloide.api.modules.plano.interfaces.http.dto;

import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import java.math.BigDecimal;
import java.time.Instant;

public record AssinaturaResponse(
        Long id,
        Long supermercadoId,
        Long planoId,
        String planoNome,
        int planoValidadeDias,
        BigDecimal planoValor,
        Integer planoLimiteFotos,
        EstadoAssinatura estado,
        Instant dataInicio,
        Instant dataFim
) {

    public static AssinaturaResponse from(Assinatura assinatura) {
        return new AssinaturaResponse(
                assinatura.id(),
                assinatura.supermercadoId(),
                assinatura.planoId(),
                assinatura.planoNome(),
                assinatura.planoValidadeDias(),
                assinatura.planoValor(),
                assinatura.planoLimiteFotos(),
                assinatura.estado(),
                assinatura.dataInicio(),
                assinatura.dataFim()
        );
    }
}
