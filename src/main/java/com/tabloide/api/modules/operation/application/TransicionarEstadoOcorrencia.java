package com.tabloide.api.modules.operation.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.EventoOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
import com.tabloide.api.modules.operation.domain.exceptions.OcorrenciaNaoEncontradaException;
import com.tabloide.api.modules.operation.domain.exceptions.VersaoOcorrenciaDesatualizadaException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransicionarEstadoOcorrencia {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public TransicionarEstadoOcorrencia(OcorrenciaRepository ocorrenciaRepository, AuditoriaRepository auditoriaRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Ocorrencia executar(
            Long id, Long versaoConhecida, EstadoOcorrencia novoEstado, String comentarioOpcional, Long atorId, Perfil perfilAtor) {
        Ocorrencia ocorrencia = ocorrenciaRepository.buscarPorId(id).orElseThrow(OcorrenciaNaoEncontradaException::new);

        if (!ocorrencia.possuiVersao(versaoConhecida)) {
            throw new VersaoOcorrenciaDesatualizadaException();
        }

        String antes = ocorrencia.resumoParaAuditoria();
        EstadoOcorrencia estadoAnterior = ocorrencia.estado();
        Instant agora = Instant.now();

        ocorrencia.transicionarPara(novoEstado, agora);
        Ocorrencia salva = ocorrenciaRepository.salvar(ocorrencia);

        ocorrenciaRepository.registrarEvento(
                EventoOcorrencia.transicao(salva.id(), estadoAnterior, salva.estado(), comentarioOpcional, atorId, agora));

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salva.supermercadoId(), "OCORRENCIA_TRANSICIONADA", "Ocorrencia", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }
}
