package com.tabloide.api.modules.analytics.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventoAcessoJpaRepository extends JpaRepository<EventoAcessoJpaEntity, Long> {

    boolean existsByEventoTecnicoId(String eventoTecnicoId);

    @Query("""
            SELECT e.supermercadoId AS supermercadoId, COUNT(e) AS quantidade
            FROM EventoAcessoJpaEntity e
            WHERE e.instante >= :inicio AND e.instante <= :fim
            GROUP BY e.supermercadoId
            """)
    List<ContagemProjecao> contarPorSupermercadoNoPeriodo(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    interface ContagemProjecao {
        Long getSupermercadoId();

        long getQuantidade();
    }
}
