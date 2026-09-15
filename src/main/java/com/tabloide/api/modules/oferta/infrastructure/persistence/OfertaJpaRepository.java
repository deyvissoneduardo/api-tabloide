package com.tabloide.api.modules.oferta.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaJpaRepository extends JpaRepository<OfertaJpaEntity, Long> {

    Optional<OfertaJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);
}
