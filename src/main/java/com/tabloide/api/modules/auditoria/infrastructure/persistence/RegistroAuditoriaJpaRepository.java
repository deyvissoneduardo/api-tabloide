package com.tabloide.api.modules.auditoria.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RegistroAuditoriaJpaRepository
        extends JpaRepository<RegistroAuditoriaJpaEntity, Long>, JpaSpecificationExecutor<RegistroAuditoriaJpaEntity> {
}
