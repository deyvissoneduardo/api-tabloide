package com.tabloide.api.modules.plano.infrastructure.persistence;

import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PlanoRepositoryAdapter implements PlanoRepository {

    private final PlanoJpaRepository jpaRepository;

    public PlanoRepositoryAdapter(PlanoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Plano> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(PlanoRepositoryAdapter::paraDominio);
    }

    private static Plano paraDominio(PlanoJpaEntity entidade) {
        return new Plano(
                entidade.getId(),
                entidade.getNome(),
                entidade.getValidadeDias(),
                entidade.getValor(),
                entidade.getLimiteFotos()
        );
    }
}
