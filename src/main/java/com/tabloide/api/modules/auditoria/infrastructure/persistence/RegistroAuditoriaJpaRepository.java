package com.tabloide.api.modules.auditoria.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAuditoriaJpaRepository extends JpaRepository<RegistroAuditoriaJpaEntity, Long> {
}
