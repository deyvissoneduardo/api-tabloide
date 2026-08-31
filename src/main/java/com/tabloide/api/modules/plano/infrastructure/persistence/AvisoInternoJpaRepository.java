package com.tabloide.api.modules.plano.infrastructure.persistence;

import com.tabloide.api.modules.plano.domain.TipoAvisoAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisoInternoJpaRepository extends JpaRepository<AvisoInternoJpaEntity, Long> {

    boolean existsByAssinaturaIdAndTipo(Long assinaturaId, TipoAvisoAssinatura tipo);
}
