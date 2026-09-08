package com.tabloide.api.modules.produto.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.CategoriaRepository;
import com.tabloide.api.modules.categoria.domain.exceptions.CategoriaNaoEncontradaException;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.produto.domain.exceptions.ProdutoNaoEncontradoException;
import com.tabloide.api.modules.produto.domain.exceptions.CategoriasProdutoInvalidasException;
import com.tabloide.api.modules.produto.domain.exceptions.VersaoProdutoDesatualizadaException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AssociarCategoriasAoProduto {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public AssociarCategoriasAoProduto(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository,
                                       SupermercadoRepository supermercadoRepository, AuditoriaRepository auditoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Produto executar(Long supermercadoId, Long produtoId, Long versaoConhecida, Set<Long> categoriaIds,
                            Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        validarEscopo(supermercadoId, supermercadoIdAtor);
        validarSupermercado(supermercadoId);

        Produto produto = produtoRepository.buscarPorIdESupermercado(produtoId, supermercadoId)
                .orElseThrow(ProdutoNaoEncontradoException::new);
        validarVersao(produto, versaoConhecida);
        validarCategorias(categoriaIds, supermercadoId);

        String antes = produto.resumoParaAuditoria();
        Instant agora = Instant.now();
        produto.substituirCategorias(categoriaIds, agora);
        Produto salvo = produtoRepository.salvar(produto);
        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "CATEGORIAS_PRODUTO_ALTERADAS", "Produto", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora));
        return salvo;
    }

    private void validarEscopo(Long supermercadoId, Long supermercadoIdAtor) {
        if (!Objects.equals(supermercadoId, supermercadoIdAtor)) {
            throw new ProdutoNaoEncontradoException();
        }
    }

    private void validarSupermercado(Long supermercadoId) {
        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();
    }

    private void validarVersao(Produto produto, Long versaoConhecida) {
        if (!produto.possuiVersao(versaoConhecida)) {
            throw new VersaoProdutoDesatualizadaException();
        }
    }

    private void validarCategorias(Set<Long> categoriaIds, Long supermercadoId) {
        if (categoriasNaoForamInformadas(categoriaIds)) {
            throw new CategoriasProdutoInvalidasException();
        }
        for (Long categoriaId : categoriaIds) {
            Categoria categoria = categoriaRepository.buscarPorIdESupermercado(categoriaId, supermercadoId)
                    .orElseThrow(CategoriaNaoEncontradaException::new);
            categoria.validarNovaAssociacao();
        }
    }

    private boolean categoriasNaoForamInformadas(Set<Long> categoriaIds) {
        return categoriaIds == null || categoriaIds.isEmpty() || categoriaIds.stream().anyMatch(Objects::isNull);
    }
}
