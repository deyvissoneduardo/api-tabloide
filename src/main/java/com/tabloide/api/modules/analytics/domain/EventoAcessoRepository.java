package com.tabloide.api.modules.analytics.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EventoAcessoRepository {

    // RN-009 (QR Codes): reenvio técnico do mesmo evento deve ser idempotente — eventos com o
    // mesmo eventoTecnicoId não são duplicados.
    void registrar(EventoAcesso evento);

    List<ContagemPorSupermercado> contarPorSupermercadoNoPeriodo(Instant inicio, Instant fim);

    // US-107: identificar qual QR Code (ou outro recurso) originou os acessos no período,
    // agrupando por recursoId. Chaves ausentes no mapa significam zero acessos.
    Map<Long, Long> contarPorRecursoNoPeriodo(TipoEventoAcesso tipo, Collection<Long> recursoIds, Instant inicio, Instant fim);
}
