package com.tabloide.api.modules.conteudogeral.application;

import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;

public record DadosConteudoGeral(TipoConteudoGeral tipo, String titulo, String corpo) {
}
