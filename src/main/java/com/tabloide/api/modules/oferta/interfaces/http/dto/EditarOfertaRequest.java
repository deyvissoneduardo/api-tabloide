package com.tabloide.api.modules.oferta.interfaces.http.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record EditarOfertaRequest(
        @NotNull Long versao,
        @NotNull Long produtoId,
        @NotEmpty Set<@NotNull Long> lojaIds,
        @NotNull @Digits(integer = 10, fraction = 2) BigDecimal precoNormal,
        @NotNull @Digits(integer = 10, fraction = 2) BigDecimal precoPromocional,
        @NotNull Instant inicio,
        @NotNull Instant fim,
        @Size(max = 500) String condicoes
) {
}
