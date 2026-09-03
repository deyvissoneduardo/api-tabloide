package com.tabloide.api.modules.produto.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoJpaRepository extends JpaRepository<ProdutoJpaEntity, Long> {
}
