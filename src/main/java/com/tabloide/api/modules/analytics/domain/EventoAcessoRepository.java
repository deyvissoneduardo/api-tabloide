package com.tabloide.api.modules.analytics.domain;

import java.time.Instant;
import java.util.List;

public interface EventoAcessoRepository {

    // RN-009 (QR Codes): reenvio técnico do mesmo evento deve ser idempotente — eventos com o
    // mesmo eventoTecnicoId não são duplicados.
    void registrar(EventoAcesso evento);

    List<ContagemPorSupermercado> contarPorSupermercadoNoPeriodo(Instant inicio, Instant fim);
}
