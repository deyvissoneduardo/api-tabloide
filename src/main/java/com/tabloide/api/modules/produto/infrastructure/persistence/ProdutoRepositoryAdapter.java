package com.tabloide.api.modules.produto.infrastructure.persistence;

import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProdutoRepositoryAdapter implements ProdutoRepository {

    private final ProdutoJpaRepository jpaRepository;

    public ProdutoRepositoryAdapter(ProdutoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Produto salvar(Produto produto) {
        ProdutoJpaEntity entidade = produto.id() == null
                ? new ProdutoJpaEntity(
                        null,
                        produto.supermercadoId(),
                        produto.nome(),
                        produto.categoriaIds(),
                        produto.marca(),
                        produto.descricao(),
                        produto.peso(),
                        produto.unidade(),
                        produto.volume(),
                        produto.estado(),
                        null,
                        produto.criadoEm(),
                        produto.atualizadoEm()
                )
                : atualizar(jpaRepository.findById(produto.id()).orElseThrow(() ->
                        new IllegalStateException("Produto " + produto.id() + " não encontrado para atualização")), produto);

        return paraDominio(jpaRepository.save(entidade));
    }

    private static ProdutoJpaEntity atualizar(ProdutoJpaEntity entidade, Produto produto) {
        entidade.setNome(produto.nome());
        entidade.setCategoriaIds(produto.categoriaIds());
        entidade.setMarca(produto.marca());
        entidade.setDescricao(produto.descricao());
        entidade.setPeso(produto.peso());
        entidade.setUnidade(produto.unidade());
        entidade.setVolume(produto.volume());
        entidade.setEstado(produto.estado());
        entidade.setAtualizadoEm(produto.atualizadoEm());
        return entidade;
    }

    private static Produto paraDominio(ProdutoJpaEntity entidade) {
        return new Produto(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getNome(),
                entidade.getCategoriaIds(),
                entidade.getMarca(),
                entidade.getDescricao(),
                entidade.getPeso(),
                entidade.getUnidade(),
                entidade.getVolume(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
