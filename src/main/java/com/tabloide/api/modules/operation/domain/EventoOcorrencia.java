package com.tabloide.api.modules.operation.domain;

import java.time.Instant;

public record EventoOcorrencia(
        Long id,
        Long ocorrenciaId,
        TipoEventoOcorrencia tipo,
        EstadoOcorrencia estadoAnterior,
        EstadoOcorrencia estadoNovo,
        String comentario,
        Long autorId,
        Instant criadoEm
) {

    public static EventoOcorrencia transicao(
            Long ocorrenciaId, EstadoOcorrencia estadoAnterior, EstadoOcorrencia estadoNovo, String comentario, Long autorId, Instant agora) {
        return new EventoOcorrencia(
                null, ocorrenciaId, TipoEventoOcorrencia.TRANSICAO_ESTADO, estadoAnterior, estadoNovo, comentario, autorId, agora);
    }

    public static EventoOcorrencia comentario(Long ocorrenciaId, String comentario, Long autorId, Instant agora) {
        return new EventoOcorrencia(null, ocorrenciaId, TipoEventoOcorrencia.COMENTARIO, null, null, comentario, autorId, agora);
    }
}
