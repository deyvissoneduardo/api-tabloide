package com.tabloide.api.modules.campanha.application;

import java.time.Instant;
import java.util.Set;

public record DadosCampanha(
        String nome,
        String descricao,
        Set<Long> lojaIds,
        Set<Long> ofertaIds,
        Instant inicio,
        Instant fim,
        boolean confirmarPublicacao
) {
}
