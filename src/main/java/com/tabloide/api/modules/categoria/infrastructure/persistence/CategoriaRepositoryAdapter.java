package com.tabloide.api.modules.categoria.infrastructure.persistence;

import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.CategoriaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CategoriaRepositoryAdapter implements CategoriaRepository {

    private final CategoriaJpaRepository jpaRepository;

    public CategoriaRepositoryAdapter(CategoriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Categoria> buscarPorIdESupermercado(Long id, Long supermercadoId) {
        return jpaRepository.findByIdAndSupermercadoId(id, supermercadoId).map(CategoriaRepositoryAdapter::paraDominio);
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        CategoriaJpaEntity entidade = categoria.id() == null
                ? new CategoriaJpaEntity(
                        null,
                        categoria.supermercadoId(),
                        categoria.nome(),
                        categoria.descricao(),
                        categoria.estado(),
                        null,
                        categoria.criadoEm(),
                        categoria.atualizadoEm()
                )
                : atualizar(jpaRepository.findById(categoria.id()).orElseThrow(() ->
                        new IllegalStateException("Categoria " + categoria.id() + " não encontrada para atualização")), categoria);

        return paraDominio(jpaRepository.save(entidade));
    }

    private static CategoriaJpaEntity atualizar(CategoriaJpaEntity entidade, Categoria categoria) {
        entidade.setNome(categoria.nome());
        entidade.setDescricao(categoria.descricao());
        entidade.setEstado(categoria.estado());
        entidade.setAtualizadoEm(categoria.atualizadoEm());
        return entidade;
    }

    private static Categoria paraDominio(CategoriaJpaEntity entidade) {
        return new Categoria(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getNome(),
                entidade.getDescricao(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
