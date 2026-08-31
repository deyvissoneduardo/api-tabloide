package com.tabloide.api.modules.operation.application;

import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
import com.tabloide.api.modules.operation.domain.exceptions.OcorrenciaNaoEncontradaException;
import org.springframework.stereotype.Component;

@Component
public class BuscarOcorrenciaPorId {

    private final OcorrenciaRepository ocorrenciaRepository;

    public BuscarOcorrenciaPorId(OcorrenciaRepository ocorrenciaRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
    }

    public OcorrenciaComHistorico executar(Long id) {
        Ocorrencia ocorrencia = ocorrenciaRepository.buscarPorId(id).orElseThrow(OcorrenciaNaoEncontradaException::new);
        return new OcorrenciaComHistorico(ocorrencia, ocorrenciaRepository.listarEventos(id));
    }
}
