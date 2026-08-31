package com.tabloide.api.modules.plano.infrastructure.persistence;

import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssinaturaJpaRepository extends JpaRepository<AssinaturaJpaEntity, Long> {

    Optional<AssinaturaJpaEntity> findFirstBySupermercadoIdAndEstadoIn(Long supermercadoId, List<EstadoAssinatura> estados);
}
