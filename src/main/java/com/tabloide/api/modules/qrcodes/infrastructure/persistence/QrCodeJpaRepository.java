package com.tabloide.api.modules.qrcodes.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrCodeJpaRepository extends JpaRepository<QrCodeJpaEntity, Long> {

    Optional<QrCodeJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);

    boolean existsByNomeNormalizadoAndSupermercadoId(String nomeNormalizado, Long supermercadoId);

    Page<QrCodeJpaEntity> findByLojaIdAndSupermercadoId(Long lojaId, Long supermercadoId, Pageable pageable);
}
