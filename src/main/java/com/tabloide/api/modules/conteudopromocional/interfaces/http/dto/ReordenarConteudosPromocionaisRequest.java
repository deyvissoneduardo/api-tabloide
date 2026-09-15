package com.tabloide.api.modules.conteudopromocional.interfaces.http.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReordenarConteudosPromocionaisRequest(
        @NotEmpty List<@NotNull Long> idsEmOrdem
) {
}
