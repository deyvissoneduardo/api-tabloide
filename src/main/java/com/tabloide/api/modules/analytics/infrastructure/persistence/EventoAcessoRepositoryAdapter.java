package com.tabloide.api.modules.analytics.infrastructure.persistence;

import com.tabloide.api.modules.analytics.domain.ContagemPorSupermercado;
import com.tabloide.api.modules.analytics.domain.EventoAcesso;
import com.tabloide.api.modules.analytics.domain.EventoAcessoRepository;
import java.time.Instant;
import java.util.List;
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

    private boolean jaRegistrado(String eventoTecnicoId) {
        return eventoTecnicoId != null && jpaRepository.existsByEventoTecnicoId(eventoTecnicoId);
    }
}
