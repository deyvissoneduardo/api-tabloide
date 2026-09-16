package com.tabloide.api.modules.publicacao.interfaces.http.dto;

import com.tabloide.api.modules.publicacao.application.LojaPublica;

public record LojaPublicaResponse(Long supermercadoId, String nomeSupermercado, Long lojaId, String nomeLoja) {

    public static LojaPublicaResponse from(LojaPublica lojaPublica) {
        return new LojaPublicaResponse(
                lojaPublica.supermercadoId(), lojaPublica.nomeSupermercado(), lojaPublica.lojaId(), lojaPublica.nomeLoja()
        );
    }
}
