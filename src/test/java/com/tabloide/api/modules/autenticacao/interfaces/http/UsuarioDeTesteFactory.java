package com.tabloide.api.modules.autenticacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;

final class UsuarioDeTesteFactory {

    private UsuarioDeTesteFactory() {
    }

    static UsuarioJpaEntity criarDono(
            UsuarioJpaRepository usuarioJpaRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String senha,
            String cnpj
    ) {
        return criarDono(usuarioJpaRepository, passwordEncoder, email, senha, cnpj, 99L);
    }

    static UsuarioJpaEntity criarDono(
            UsuarioJpaRepository usuarioJpaRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String senha,
            String cnpj,
            Long supermercadoId
    ) {
        return criar(usuarioJpaRepository, passwordEncoder, email, senha, Perfil.DONO, supermercadoId, cnpj);
    }

    static UsuarioJpaEntity criarOperador(
            UsuarioJpaRepository usuarioJpaRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String senha,
            String cnpj,
            Long supermercadoId
    ) {
        return criar(usuarioJpaRepository, passwordEncoder, email, senha, Perfil.OPERADOR, supermercadoId, cnpj);
    }

    private static UsuarioJpaEntity criar(
            UsuarioJpaRepository usuarioJpaRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String senha,
            Perfil perfil,
            Long supermercadoId,
            String cnpj
    ) {
        Instant agora = Instant.now();
        UsuarioJpaEntity entidade = new UsuarioJpaEntity(
                null,
                email,
                passwordEncoder.encode(senha),
                perfil,
                supermercadoId,
                cnpj,
                true,
                0,
                null,
                agora,
                agora
        );
        return usuarioJpaRepository.save(entidade);
    }
}
