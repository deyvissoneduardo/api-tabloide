package com.tabloide.api.modules.conteudopromocional.application;

import com.tabloide.api.modules.conteudopromocional.domain.NivelAviso;
import com.tabloide.api.modules.conteudopromocional.domain.TipoConteudoPromocional;
import java.time.Instant;
import java.util.Set;

public record DadosConteudoPromocional(
        TipoConteudoPromocional tipo,
        String titulo,
        String texto,
        NivelAviso nivel,
        String destino,
        Set<Long> lojaIds,
        Instant inicio,
        Instant fim
) {
}
