package com.tabloide.api.modules.plano.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoJpaRepository extends JpaRepository<PlanoJpaEntity, Long> {

    boolean existsByNomeNormalizadoAndExcluidoEmIsNull(String nomeNormalizado);

    boolean existsByNomeNormalizadoAndIdNotAndExcluidoEmIsNull(String nomeNormalizado, Long id);
}
