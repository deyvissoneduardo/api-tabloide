package com.tabloide.api.modules.autenticacao.interfaces.http.dto;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import java.time.Instant;

public record LoginResponse(String token, Perfil perfil, Instant expiraEm) {
}
