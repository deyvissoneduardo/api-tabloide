package com.tabloide.api.modules.analytics.domain;

import java.time.Instant;

// RN (Analytics, dashboard e relatórios): cada evento contém identificador único, supermercado,
// loja quando aplicável, recurso, data/hora UTC e origem; consumidor nunca é identificado.
public record EventoAcesso(
        Long id,
        TipoEventoAcesso tipo,
        Long supermercadoId,
        Long lojaId,
        Long recursoId,
        String eventoTecnicoId,
        Instant instante
) {

    public static EventoAcesso registrar(TipoEventoAcesso tipo, Long supermercadoId, Long lojaId, Long recursoId, String eventoTecnicoId, Instant agora) {
        return new EventoAcesso(null, tipo, supermercadoId, lojaId, recursoId, eventoTecnicoId, agora);
    }
}
