package com.tabloide.api.modules.analytics.infrastructure.persistence;

import com.tabloide.api.modules.analytics.domain.ContagemPorSupermercado;
import com.tabloide.api.modules.analytics.domain.EventoAcesso;
import com.tabloide.api.modules.analytics.domain.EventoAcessoRepository;
import com.tabloide.api.modules.analytics.domain.TipoEventoAcesso;
import com.tabloide.api.modules.analytics.infrastructure.persistence.EventoAcessoJpaRepository.ContagemPorRecursoProjecao;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class EventoAcessoRepositoryAdapter implements EventoAcessoRepository {

    private final EventoAcessoJpaRepository jpaRepository;

    public EventoAcessoRepositoryAdapter(EventoAcessoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void registrar(EventoAcesso evento) {
        if (jaRegistrado(evento.eventoTecnicoId())) {
            return;
        }
        try {
            jpaRepository.save(new EventoAcessoJpaEntity(
                    null, evento.tipo(), evento.supermercadoId(), evento.lojaId(), evento.recursoId(),
                    evento.eventoTecnicoId(), evento.instante()
            ));
        } catch (DataIntegrityViolationException reenvioConcorrenteDoMesmoEvento) {
            // RN-009: reenvio técnico do mesmo evento (mesmo eventoTecnicoId) deve ser idempotente.
        }
    }

    @Override
    public List<ContagemPorSupermercado> contarPorSupermercadoNoPeriodo(Instant inicio, Instant fim) {
        return jpaRepository.contarPorSupermercadoNoPeriodo(inicio, fim).stream()
                .map(projecao -> new ContagemPorSupermercado(projecao.getSupermercadoId(), projecao.getQuantidade()))
                .toList();
    }

    @Override
    public Map<Long, Long> contarPorRecursoNoPeriodo(TipoEventoAcesso tipo, Collection<Long> recursoIds, Instant inicio, Instant fim) {
        if (recursoIds.isEmpty()) {
            return Map.of();
        }
        return jpaRepository.contarPorRecursoNoPeriodo(tipo, recursoIds, inicio, fim).stream()
                .collect(Collectors.toMap(ContagemPorRecursoProjecao::getRecursoId, ContagemPorRecursoProjecao::getQuantidade));
    }

    private boolean jaRegistrado(String eventoTecnicoId) {
        return eventoTecnicoId != null && jpaRepository.existsByEventoTecnicoId(eventoTecnicoId);
    }
}
