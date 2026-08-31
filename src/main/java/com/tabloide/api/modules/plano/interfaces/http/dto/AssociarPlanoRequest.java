package com.tabloide.api.modules.plano.interfaces.http.dto;

import jakarta.validation.constraints.NotNull;

public record AssociarPlanoRequest(
        @NotNull
        Long planoId
) {
}
