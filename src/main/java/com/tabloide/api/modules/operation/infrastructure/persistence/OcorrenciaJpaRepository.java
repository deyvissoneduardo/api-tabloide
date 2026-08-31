package com.tabloide.api.modules.operation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OcorrenciaJpaRepository extends JpaRepository<OcorrenciaJpaEntity, Long>, JpaSpecificationExecutor<OcorrenciaJpaEntity> {
}
