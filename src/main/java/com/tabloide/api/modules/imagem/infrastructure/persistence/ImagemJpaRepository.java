package com.tabloide.api.modules.imagem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImagemJpaRepository extends JpaRepository<ImagemJpaEntity, Long>, JpaSpecificationExecutor<ImagemJpaEntity> {

    long countBySupermercadoIdAndExcluidoEmIsNull(Long supermercadoId);
}
