package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenha;
import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenhaRepository;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.TokenOpaco;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TokenRedefinicaoInvalidoOuExpiradoException;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RedefinirSenha {

    private final RedefinicaoSenhaRepository redefinicaoSenhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SessaoRepository sessaoRepository;
    private final PasswordEncoder passwordEncoder;

    public RedefinirSenha(
            RedefinicaoSenhaRepository redefinicaoSenhaRepository,
            UsuarioRepository usuarioRepository,
            SessaoRepository sessaoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.redefinicaoSenhaRepository = redefinicaoSenhaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sessaoRepository = sessaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void redefinir(String tokenBruto, String novaSenha) {
        Instant agora = Instant.now();
        String tokenHash = TokenOpaco.hash(tokenBruto);

        RedefinicaoSenha redefinicao = redefinicaoSenhaRepository.buscarPorTokenHash(tokenHash)
                .filter(r -> r.estaValida(agora))
                .orElseThrow(TokenRedefinicaoInvalidoOuExpiradoException::new);

        Usuario usuario = usuarioRepository.buscarPorId(redefinicao.usuarioId())
                .orElseThrow(TokenRedefinicaoInvalidoOuExpiradoException::new);

        usuario.alterarSenha(passwordEncoder.encode(novaSenha), agora);
        usuarioRepository.salvar(usuario);

        redefinicao.marcarComoUsado(agora);
        redefinicaoSenhaRepository.salvar(redefinicao);

        sessaoRepository.revogarTodasDoUsuario(usuario.id(), agora);
    }
}
