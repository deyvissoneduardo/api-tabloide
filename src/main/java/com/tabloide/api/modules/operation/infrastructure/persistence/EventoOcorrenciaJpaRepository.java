package com.tabloide.api.modules.operation.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoOcorrenciaJpaRepository extends JpaRepository<EventoOcorrenciaJpaEntity, Long> {

    List<EventoOcorrenciaJpaEntity> findByOcorrenciaIdOrderByCriadoEmAsc(Long ocorrenciaId);
}
