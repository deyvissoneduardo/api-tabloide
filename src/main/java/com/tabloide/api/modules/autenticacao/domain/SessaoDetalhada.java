package com.tabloide.api.modules.autenticacao.domain;

import java.time.Instant;

public record SessaoDetalhada(
        String jti,
        String email,
        Perfil perfil,
        Long supermercadoId,
        Instant criadoEm,
        Instant expiraEm,
        Instant ultimoUsoEm,
        Instant revogadaEm
) {

    public boolean estaAtiva(Instant agora) {
        return revogadaEm == null && agora.isBefore(expiraEm);
    }
}
