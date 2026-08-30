package com.tabloide.api.modules.autenticacao.interfaces.http.dto;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada;
import java.time.Instant;

public record SessaoResumoResponse(
        String id,
        String email,
        Perfil perfil,
        Long supermercadoId,
        Instant criadoEm,
        Instant expiraEm,
        Instant ultimoUsoEm,
        boolean ativa
) {

    public static SessaoResumoResponse from(SessaoDetalhada sessao) {
        return new SessaoResumoResponse(
                sessao.jti(),
                sessao.email(),
                sessao.perfil(),
                sessao.supermercadoId(),
                sessao.criadoEm(),
                sessao.expiraEm(),
                sessao.ultimoUsoEm(),
                sessao.estaAtiva(Instant.now())
        );
    }
}
