package com.tabloide.api.modules.conteudogeral.interfaces.http.dto;

import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import java.time.Instant;

public record ConteudoGeralResponse(
        Long id,
        TipoConteudoGeral tipo,
        String titulo,
        String corpo,
        EstadoConteudoGeral estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm,
        Instant publicadoEm
) {

    public static ConteudoGeralResponse from(ConteudoGeral conteudoGeral) {
        return new ConteudoGeralResponse(
                conteudoGeral.id(),
                conteudoGeral.tipo(),
                conteudoGeral.titulo(),
                conteudoGeral.corpo(),
                conteudoGeral.estado(),
                conteudoGeral.versao(),
                conteudoGeral.criadoEm(),
                conteudoGeral.atualizadoEm(),
                conteudoGeral.publicadoEm()
        );
    }
}
