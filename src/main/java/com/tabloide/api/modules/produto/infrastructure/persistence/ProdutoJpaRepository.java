package com.tabloide.api.modules.produto.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProdutoJpaRepository extends JpaRepository<ProdutoJpaEntity, Long> {
    Optional<ProdutoJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);
}
