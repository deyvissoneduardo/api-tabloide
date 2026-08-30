package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.Sessao;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.CredenciaisInvalidasException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticarUsuario {

    private final UsuarioRepository usuarioRepository;
    private final SessaoRepository sessaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AutenticacaoProperties propriedades;

    public AutenticarUsuario(
            UsuarioRepository usuarioRepository,
            SessaoRepository sessaoRepository,
            PasswordEncoder passwordEncoder,
            AutenticacaoProperties propriedades
    ) {
        this.usuarioRepository = usuarioRepository;
        this.sessaoRepository = sessaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.propriedades = propriedades;
    }

    public SessaoAutenticada autenticar(String email, String senha) {
        Instant agora = Instant.now();
        Usuario usuario = usuarioRepository.buscarPorEmail(normalizarEmail(email))
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!usuario.estaAtivo() || usuario.estaBloqueadoTemporariamente(agora)) {
            throw new CredenciaisInvalidasException();
        }

        if (!passwordEncoder.matches(senha, usuario.senhaHash())) {
            usuario.registrarTentativaInvalida(
                    agora,
                    propriedades.login().maxTentativas(),
                    propriedades.login().bloqueioTemporario()
            );
            usuarioRepository.salvar(usuario);
            throw new CredenciaisInvalidasException();
        }

        usuario.registrarLoginBemSucedido(agora);
        usuarioRepository.salvar(usuario);

        Sessao sessao = Sessao.iniciar(
                usuario.id(),
                UUID.randomUUID().toString(),
                agora,
                propriedades.sessao().duracaoMaxima()
        );
        sessaoRepository.salvar(sessao);

        return new SessaoAutenticada(usuario, sessao);
    }

    private static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
