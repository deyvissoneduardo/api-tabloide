package com.tabloide.api.modules.autenticacao.application;

import java.time.Instant;

public record ResultadoVerificacaoRedefinicao(String token, Instant expiraEm) {
}
