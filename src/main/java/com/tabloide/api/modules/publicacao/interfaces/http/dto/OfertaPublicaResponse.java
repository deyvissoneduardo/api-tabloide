package com.tabloide.api.modules.publicacao.interfaces.http.dto;

import com.tabloide.api.modules.publicacao.application.OfertaPublica;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record OfertaPublicaResponse(
        Long ofertaId,
        Long produtoId,
        String nomeProduto,
        String marca,
        String descricao,
        Set<Long> categoriaIds,
        BigDecimal precoNormal,
        BigDecimal precoPromocional,
        Integer percentualDesconto,
        Instant validadeAte,
        String condicoes
) {

    public static OfertaPublicaResponse from(OfertaPublica ofertaPublica) {
        return new OfertaPublicaResponse(
                ofertaPublica.ofertaId(),
                ofertaPublica.produtoId(),
                ofertaPublica.nomeProduto(),
                ofertaPublica.marca(),
                ofertaPublica.descricao(),
                ofertaPublica.categoriaIds(),
                ofertaPublica.precoNormal(),
                ofertaPublica.precoPromocional(),
                ofertaPublica.percentualDesconto(),
                ofertaPublica.validadeAte(),
                ofertaPublica.condicoes()
        );
    }
}
