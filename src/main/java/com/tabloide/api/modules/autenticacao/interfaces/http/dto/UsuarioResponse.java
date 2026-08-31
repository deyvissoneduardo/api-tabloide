package com.tabloide.api.modules.autenticacao.interfaces.http.dto;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import java.time.Instant;

public record UsuarioResponse(
        Long id,
        String email,
        Perfil perfil,
        boolean ativo,
        Instant bloqueadoAte,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.id(),
                usuario.email(),
                usuario.perfil(),
                usuario.estaAtivo(),
                usuario.bloqueadoAte(),
                usuario.criadoEm(),
                usuario.atualizadoEm()
        );
    }
}
