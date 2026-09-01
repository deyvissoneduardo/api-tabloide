package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.CredenciaisInvalidasException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.EmailJaCadastradoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.NenhumaAlteracaoInformadaException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.UsuarioNaoEncontradoException;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AlterarPropriosDados {

    private final UsuarioRepository usuarioRepository;
    private final SessaoRepository sessaoRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final PasswordEncoder passwordEncoder;

    public AlterarPropriosDados(
            UsuarioRepository usuarioRepository,
            SessaoRepository sessaoRepository,
            AuditoriaRepository auditoriaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.sessaoRepository = sessaoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario executar(Long usuarioId, DadosAlteracaoPropriosDados dados, Perfil perfilAtor, Long supermercadoIdAtor) {
        boolean trocaEmail = dados.novoEmail() != null && !dados.novoEmail().isBlank();
        boolean trocaSenha = dados.novaSenha() != null && !dados.novaSenha().isBlank();
        if (!trocaEmail && !trocaSenha) {
            throw new NenhumaAlteracaoInformadaException();
        }

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId).orElseThrow(UsuarioNaoEncontradoException::new);

        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.senhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        String antes = usuario.resumoParaAuditoria();
        Instant agora = Instant.now();

        if (trocaEmail) {
            String novoEmailNormalizado = dados.novoEmail().trim().toLowerCase();
            usuarioRepository.buscarPorEmail(novoEmailNormalizado)
                    .filter(outro -> !outro.id().equals(usuarioId))
                    .ifPresent(outro -> {
                        throw new EmailJaCadastradoException();
                    });
            usuario.alterarEmail(novoEmailNormalizado, agora);
        }

        if (trocaSenha) {
            usuario.alterarSenha(passwordEncoder.encode(dados.novaSenha()), agora);
        }

        Usuario salvo = usuarioRepository.salvar(usuario);

        // RN-013: alterar senha encerra todas as sessões do usuário; troca isolada de e-mail preserva a sessão atual.
        if (trocaSenha) {
            sessaoRepository.revogarTodasDoUsuario(salvo.id(), agora);
        }

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, usuarioId, perfilAtor, supermercadoIdAtor, "USUARIO_ALTEROU_PROPRIOS_DADOS", "Usuario", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
