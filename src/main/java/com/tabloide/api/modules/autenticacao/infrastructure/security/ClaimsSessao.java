package com.tabloide.api.modules.autenticacao.infrastructure.security;

import com.tabloide.api.modules.autenticacao.domain.Perfil;

public record ClaimsSessao(Long usuarioId, Perfil perfil, Long supermercadoId, String jti) {
}
