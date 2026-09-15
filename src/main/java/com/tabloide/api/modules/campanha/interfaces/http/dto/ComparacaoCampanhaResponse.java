package com.tabloide.api.modules.campanha.interfaces.http.dto;

import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.EstadoCampanha;
import java.time.Instant;

public record ComparacaoCampanhaResponse(
        Long id,
        String nome,
        Instant inicio,
        Instant fim,
        EstadoCampanha estado,
        int quantidadeLojas,
        int quantidadeOfertas
) {

    public static ComparacaoCampanhaResponse from(Campanha campanha) {
        return new ComparacaoCampanhaResponse(
                campanha.id(),
                campanha.nome(),
                campanha.inicio(),
                campanha.fim(),
                campanha.estado(),
                campanha.quantidadeLojas(),
                campanha.quantidadeOfertas()
        );
    }
}
