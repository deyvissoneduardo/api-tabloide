package com.tabloide.api.modules.campanha.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CampanhaJpaRepository extends JpaRepository<CampanhaJpaEntity, Long> {

    Optional<CampanhaJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);

    List<CampanhaJpaEntity> findByIdInAndSupermercadoId(Set<Long> ids, Long supermercadoId);

    Page<CampanhaJpaEntity> findBySupermercadoId(Long supermercadoId, Pageable pageable);

    @Query("""
            SELECT COUNT(c) > 0 FROM CampanhaJpaEntity c JOIN c.ofertaIds oid
            WHERE c.supermercadoId = :supermercadoId
              AND oid = :ofertaId
              AND c.estado NOT IN (com.tabloide.api.modules.campanha.domain.EstadoCampanha.CANCELADA, com.tabloide.api.modules.campanha.domain.EstadoCampanha.EXPIRADA)
              AND (:campanhaIdParaExcluir IS NULL OR c.id <> :campanhaIdParaExcluir)
            """)
    boolean existeOfertaEmCampanhaAtiva(
            @Param("supermercadoId") Long supermercadoId,
            @Param("ofertaId") Long ofertaId,
            @Param("campanhaIdParaExcluir") Long campanhaIdParaExcluir
    );
}
