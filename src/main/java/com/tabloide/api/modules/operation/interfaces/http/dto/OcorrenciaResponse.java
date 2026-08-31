package com.tabloide.api.modules.operation.interfaces.http.dto;

import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.SeveridadeOcorrencia;
import java.time.Instant;

public record OcorrenciaResponse(
        Long id,
        String titulo,
        String descricao,
        SeveridadeOcorrencia severidade,
        EstadoOcorrencia estado,
        Long supermercadoId,
        Long responsavelId,
        Instant abertaEm,
        Instant resolvidaEm,
        Instant encerradaEm,
        Instant atualizadoEm,
        Long versao
) {

    public static OcorrenciaResponse from(Ocorrencia ocorrencia) {
        return new OcorrenciaResponse(
                ocorrencia.id(),
                ocorrencia.titulo(),
                ocorrencia.descricao(),
                ocorrencia.severidade(),
                ocorrencia.estado(),
                ocorrencia.supermercadoId(),
                ocorrencia.responsavelId(),
                ocorrencia.abertaEm(),
                ocorrencia.resolvidaEm(),
                ocorrencia.encerradaEm(),
                ocorrencia.atualizadoEm(),
                ocorrencia.versao()
        );
    }
}
