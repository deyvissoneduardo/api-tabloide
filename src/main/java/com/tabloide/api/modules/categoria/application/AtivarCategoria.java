package com.tabloide.api.modules.categoria.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.CategoriaRepository;
import com.tabloide.api.modules.categoria.domain.exceptions.CategoriaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AtivarCategoria {

    private final CategoriaRepository categoriaRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final SupermercadoRepository supermercadoRepository;

    public AtivarCategoria(CategoriaRepository categoriaRepository, AuditoriaRepository auditoriaRepository,
                           SupermercadoRepository supermercadoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.supermercadoRepository = supermercadoRepository;
    }

    @Transactional
    public Categoria executar(Long supermercadoId, Long id, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new CategoriaNaoEncontradaException();
        }

        validarSupermercadoPermiteMutacao(supermercadoId);

        Categoria categoria = categoriaRepository.buscarPorIdESupermercado(id, supermercadoId)
                .orElseThrow(CategoriaNaoEncontradaException::new);

        if (categoria.estaAtiva()) {
            return categoria;
        }

        String antes = categoria.resumoParaAuditoria();
        Instant agora = Instant.now();
        categoria.ativar(agora);
        Categoria salva = categoriaRepository.salvar(categoria);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "CATEGORIA_ATIVADA", "Categoria", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }

    private void validarSupermercadoPermiteMutacao(Long supermercadoId) {
        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();
    }
}
