package com.tabloide.api.modules.operation.interfaces.http.dto;

import com.tabloide.api.modules.operation.application.OcorrenciaComHistorico;
import java.util.List;

public record OcorrenciaDetalheResponse(OcorrenciaResponse ocorrencia, List<EventoOcorrenciaResponse> historico) {

    public static OcorrenciaDetalheResponse from(OcorrenciaComHistorico ocorrenciaComHistorico) {
        return new OcorrenciaDetalheResponse(
                OcorrenciaResponse.from(ocorrenciaComHistorico.ocorrencia()),
                ocorrenciaComHistorico.historico().stream().map(EventoOcorrenciaResponse::from).toList()
        );
    }
}
