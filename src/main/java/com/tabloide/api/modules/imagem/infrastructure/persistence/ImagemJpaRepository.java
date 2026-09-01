package com.tabloide.api.modules.imagem.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemJpaRepository extends JpaRepository<ImagemJpaEntity, Long> {

    long countBySupermercadoIdAndExcluidoEmIsNull(Long supermercadoId);

    Page<ImagemJpaEntity> findBySupermercadoIdAndNomeBuscaContainingIgnoreCase(
            Long supermercadoId, String nomeBusca, Pageable pageable);

    Page<ImagemJpaEntity> findBySupermercadoId(Long supermercadoId, Pageable pageable);
}
