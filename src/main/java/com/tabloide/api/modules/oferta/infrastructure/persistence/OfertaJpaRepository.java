package com.tabloide.api.modules.oferta.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfertaJpaRepository extends JpaRepository<OfertaJpaEntity, Long> {

    Optional<OfertaJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);

    @Query("""
            SELECT DISTINCT o FROM OfertaJpaEntity o JOIN o.lojaIds l
            WHERE l = :lojaId AND o.supermercadoId = :supermercadoId
            """)
    List<OfertaJpaEntity> findByLojaIdAndSupermercadoId(@Param("lojaId") Long lojaId, @Param("supermercadoId") Long supermercadoId);
}
