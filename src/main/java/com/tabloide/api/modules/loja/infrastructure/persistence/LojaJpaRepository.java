package com.tabloide.api.modules.loja.infrastructure.persistence;

import com.tabloide.api.modules.loja.domain.EstadoLoja;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LojaJpaRepository extends JpaRepository<LojaJpaEntity, Long> {

    Optional<LojaJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);

    boolean existsByNomeNormalizadoAndSupermercadoId(String nomeNormalizado, Long supermercadoId);

    long countBySupermercadoIdAndEstado(Long supermercadoId, EstadoLoja estado);

    Page<LojaJpaEntity> findBySupermercadoId(Long supermercadoId, Pageable pageable);
}
