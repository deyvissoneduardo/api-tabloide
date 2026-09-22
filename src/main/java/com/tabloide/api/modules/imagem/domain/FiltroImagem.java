package com.tabloide.api.modules.imagem.domain;

import java.time.Instant;

// RN-005: biblioteca pesquisável por nome e data de upload.
public record FiltroImagem(String nomeBusca, Instant dataInicio, Instant dataFim) {
}
