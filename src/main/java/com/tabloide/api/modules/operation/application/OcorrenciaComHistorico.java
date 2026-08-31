package com.tabloide.api.modules.operation.application;

import com.tabloide.api.modules.operation.domain.EventoOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import java.util.List;

public record OcorrenciaComHistorico(Ocorrencia ocorrencia, List<EventoOcorrencia> historico) {
}
