package com.tabloide.api.modules.plano.interfaces.http.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CriarPlanoRequest(
        @NotBlank String nome,
        @Min(7) int validadeDias,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 8, fraction = 2) BigDecimal valor,
        @Positive Integer limiteFotos,
        @Positive Integer limiteLojas
) {
}
