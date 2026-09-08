package com.tabloide.api.modules.categoria.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.CategoriaRepository;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CadastrarCategoria {

    private final CategoriaRepository categoriaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CadastrarCategoria(
            CategoriaRepository categoriaRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Categoria executar(Long supermercadoId, DadosCategoria dados, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new SupermercadoNaoEncontradoException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        Instant agora = Instant.now();
        Categoria categoria = Categoria.cadastrar(supermercadoId, dados.nome(), dados.descricao(), agora);
        Categoria salva = categoriaRepository.salvar(categoria);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "CATEGORIA_CADASTRADA", "Categoria", salva.id(),
                null, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
