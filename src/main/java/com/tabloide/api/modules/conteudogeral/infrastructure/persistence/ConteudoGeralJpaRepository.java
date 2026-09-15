package com.tabloide.api.modules.conteudogeral.infrastructure.persistence;

import com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConteudoGeralJpaRepository extends JpaRepository<ConteudoGeralJpaEntity, Long> {

    Optional<ConteudoGeralJpaEntity> findByTipoAndEstado(TipoConteudoGeral tipo, EstadoConteudoGeral estado);

    Page<ConteudoGeralJpaEntity> findByTipo(TipoConteudoGeral tipo, Pageable pageable);
}
