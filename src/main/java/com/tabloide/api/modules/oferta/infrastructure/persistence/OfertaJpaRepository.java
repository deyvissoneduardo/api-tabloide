package com.tabloide.api.modules.oferta.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaJpaRepository extends JpaRepository<OfertaJpaEntity, Long> {
}
