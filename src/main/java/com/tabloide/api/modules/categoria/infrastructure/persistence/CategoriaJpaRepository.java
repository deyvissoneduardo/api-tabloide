package com.tabloide.api.modules.categoria.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaJpaEntity, Long> {

    Optional<CategoriaJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);

    List<CategoriaJpaEntity> findByIdInAndSupermercadoId(Set<Long> ids, Long supermercadoId);
}
