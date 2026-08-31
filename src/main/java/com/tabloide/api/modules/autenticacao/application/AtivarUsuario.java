package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.UsuarioNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AtivarUsuario {

    private final UsuarioRepository usuarioRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public AtivarUsuario(
            UsuarioRepository usuarioRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Usuario executar(Long supermercadoId, Long usuarioId, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, perfilAtor, supermercadoIdAtor)) {
            throw new UsuarioNaoEncontradoException();
        }

        Usuario usuario = usuarioRepository.buscarPorIdESupermercado(usuarioId, supermercadoId)
                .orElseThrow(UsuarioNaoEncontradoException::new);

        if (usuario.estaAtivo()) {
            return usuario;
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        if (!supermercado.estaAtivo()) {
            throw new SupermercadoBloqueadoOuDesativadoException();
        }

        String antes = usuario.resumoParaAuditoria();
        Instant agora = Instant.now();
        usuario.ativar(agora);
        Usuario salvo = usuarioRepository.salvar(usuario);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salvo.supermercadoId(), "USUARIO_ATIVADO", "Usuario", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (perfilAtor == Perfil.SUPER_ADMIN) {
            return false;
        }
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
