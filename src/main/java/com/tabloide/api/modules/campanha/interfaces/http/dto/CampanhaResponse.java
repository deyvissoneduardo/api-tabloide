package com.tabloide.api.modules.campanha.interfaces.http.dto;

import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.EstadoCampanha;
import java.time.Instant;
import java.util.Set;

public record CampanhaResponse(
        Long id,
        Long supermercadoId,
        String nome,
        String descricao,
        Set<Long> lojaIds,
        Set<Long> ofertaIds,
        Instant inicio,
        Instant fim,
        EstadoCampanha estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static CampanhaResponse from(Campanha campanha) {
        return new CampanhaResponse(
                campanha.id(),
                campanha.supermercadoId(),
                campanha.nome(),
                campanha.descricao(),
                campanha.lojaIds(),
                campanha.ofertaIds(),
                campanha.inicio(),
                campanha.fim(),
                campanha.estado(),
                campanha.versao(),
                campanha.criadoEm(),
                campanha.atualizadoEm()
        );
    }
}
