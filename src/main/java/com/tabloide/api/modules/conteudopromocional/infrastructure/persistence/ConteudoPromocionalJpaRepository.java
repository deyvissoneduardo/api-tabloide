package com.tabloide.api.modules.conteudopromocional.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConteudoPromocionalJpaRepository extends JpaRepository<ConteudoPromocionalJpaEntity, Long> {

    Optional<ConteudoPromocionalJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);

    List<ConteudoPromocionalJpaEntity> findBySupermercadoIdOrderByPosicaoAsc(Long supermercadoId);

    long countBySupermercadoId(Long supermercadoId);
}
