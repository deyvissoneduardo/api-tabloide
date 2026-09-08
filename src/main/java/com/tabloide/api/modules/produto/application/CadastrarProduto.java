package com.tabloide.api.modules.produto.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.CategoriaRepository;
import com.tabloide.api.modules.categoria.domain.exceptions.CategoriaNaoEncontradaException;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CadastrarProduto {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CadastrarProduto(
            ProdutoRepository produtoRepository,
            CategoriaRepository categoriaRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Produto executar(Long supermercadoId, DadosProduto dados, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new SupermercadoNaoEncontradoException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        validarCategorias(dados.categoriaIds(), supermercadoId);

        Instant agora = Instant.now();
        Produto produto = Produto.cadastrar(
                supermercadoId, dados.nome(), dados.categoriaIds(), dados.marca(),
                dados.descricao(), dados.peso(), dados.unidade(), dados.volume(), agora
        );
        Produto salvo = produtoRepository.salvar(produto);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "PRODUTO_CADASTRADO", "Produto", salvo.id(),
                null, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }

    private void validarCategorias(Set<Long> categoriaIds, Long supermercadoId) {
        for (Long categoriaId : categoriaIds) {
            Categoria categoria = categoriaRepository.buscarPorIdESupermercado(categoriaId, supermercadoId)
                    .orElseThrow(CategoriaNaoEncontradaException::new);
            categoria.validarNovaAssociacao();
        }
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
