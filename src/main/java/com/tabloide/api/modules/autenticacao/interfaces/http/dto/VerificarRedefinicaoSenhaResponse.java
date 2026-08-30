package com.tabloide.api.modules.autenticacao.interfaces.http.dto;

import java.time.Instant;

public record VerificarRedefinicaoSenhaResponse(String token, Instant expiraEm) {
}
