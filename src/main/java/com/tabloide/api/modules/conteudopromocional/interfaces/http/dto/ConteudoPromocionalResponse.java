package com.tabloide.api.modules.conteudopromocional.interfaces.http.dto;

import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.EstadoConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.NivelAviso;
import com.tabloide.api.modules.conteudopromocional.domain.TipoConteudoPromocional;
import java.time.Instant;
import java.util.Set;

public record ConteudoPromocionalResponse(
        Long id,
        Long supermercadoId,
        TipoConteudoPromocional tipo,
        String titulo,
        String texto,
        NivelAviso nivel,
        String destino,
        Set<Long> lojaIds,
        Instant inicio,
        Instant fim,
        int posicao,
        EstadoConteudoPromocional estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static ConteudoPromocionalResponse from(ConteudoPromocional conteudo) {
        Instant agora = Instant.now();
        return new ConteudoPromocionalResponse(
                conteudo.id(),
                conteudo.supermercadoId(),
                conteudo.tipo(),
                conteudo.titulo(),
                conteudo.texto(),
                conteudo.nivel(),
                conteudo.destino(),
                conteudo.lojaIds(),
                conteudo.inicio(),
                conteudo.fim(),
                conteudo.posicao(),
                conteudo.estadoEfetivo(agora),
                conteudo.versao(),
                conteudo.criadoEm(),
                conteudo.atualizadoEm()
        );
    }
}
