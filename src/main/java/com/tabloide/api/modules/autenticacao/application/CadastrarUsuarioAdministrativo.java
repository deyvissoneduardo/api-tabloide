package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.EmailJaCadastradoException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CadastrarUsuarioAdministrativo {

    private final SupermercadoRepository supermercadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastrarUsuarioAdministrativo(
            SupermercadoRepository supermercadoRepository,
            UsuarioRepository usuarioRepository,
            AuditoriaRepository auditoriaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.supermercadoRepository = supermercadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario executar(
            Long supermercadoId,
            DadosNovoUsuarioAdministrativo dados,
            Long atorId,
            Perfil perfilAtor,
            Long supermercadoIdAtor
    ) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new SupermercadoNaoEncontradoException();
        }
        Usuario.validarPerfilCadastravel(dados.perfil());

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        if (!supermercado.estaAtivo()) {
            throw new SupermercadoBloqueadoOuDesativadoException();
        }

        if (usuarioRepository.buscarPorEmail(dados.email()).isPresent()) {
            throw new EmailJaCadastradoException();
        }

        Instant agora = Instant.now();
        Usuario novoUsuario = Usuario.cadastrar(
                dados.email(),
                passwordEncoder.encode(dados.senha()),
                dados.perfil(),
                supermercado.id(),
                supermercado.cnpj(),
                agora
        );
        Usuario salvo = usuarioRepository.salvar(novoUsuario);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salvo.supermercadoId(), "USUARIO_CADASTRADO", "Usuario", salvo.id(),
                null, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
