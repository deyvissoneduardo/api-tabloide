package com.tabloide.api.modules.categoria.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaJpaEntity, Long> {

    Optional<CategoriaJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);
}
