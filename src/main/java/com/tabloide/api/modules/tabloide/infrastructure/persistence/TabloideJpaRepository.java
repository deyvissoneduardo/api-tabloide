package com.tabloide.api.modules.tabloide.infrastructure.persistence;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TabloideJpaRepository extends JpaRepository<TabloideJpaEntity, Long> {

    Optional<TabloideJpaEntity> findFirstBySupermercadoIdAndInicioLessThanEqualAndFimGreaterThanEqualOrderByInicioDesc(
            Long supermercadoId, Instant agora, Instant agoraFim
    );

    default Optional<TabloideJpaEntity> findVigentePorSupermercado(Long supermercadoId, Instant agora) {
        return findFirstBySupermercadoIdAndInicioLessThanEqualAndFimGreaterThanEqualOrderByInicioDesc(supermercadoId, agora, agora);
    }
}
