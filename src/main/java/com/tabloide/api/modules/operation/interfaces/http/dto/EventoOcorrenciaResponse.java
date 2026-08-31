package com.tabloide.api.modules.operation.interfaces.http.dto;

import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.EventoOcorrencia;
import com.tabloide.api.modules.operation.domain.TipoEventoOcorrencia;
import java.time.Instant;

public record EventoOcorrenciaResponse(
        Long id,
        TipoEventoOcorrencia tipo,
        EstadoOcorrencia estadoAnterior,
        EstadoOcorrencia estadoNovo,
        String comentario,
        Long autorId,
        Instant criadoEm
) {

    public static EventoOcorrenciaResponse from(EventoOcorrencia evento) {
        return new EventoOcorrenciaResponse(
                evento.id(),
                evento.tipo(),
                evento.estadoAnterior(),
                evento.estadoNovo(),
                evento.comentario(),
                evento.autorId(),
                evento.criadoEm()
        );
    }
}
