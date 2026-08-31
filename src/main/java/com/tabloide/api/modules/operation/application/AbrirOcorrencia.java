package com.tabloide.api.modules.operation.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.operation.domain.EventoOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AbrirOcorrencia {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public AbrirOcorrencia(OcorrenciaRepository ocorrenciaRepository, AuditoriaRepository auditoriaRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Ocorrencia executar(DadosAbrirOcorrencia dados, Long atorId, Perfil perfilAtor) {
        Instant agora = Instant.now();
        Long responsavelId = dados.responsavelId() != null ? dados.responsavelId() : atorId;

        Ocorrencia ocorrencia = Ocorrencia.abrir(
                dados.titulo(), dados.descricao(), dados.severidade(), dados.supermercadoId(), responsavelId, agora);
        Ocorrencia salva = ocorrenciaRepository.salvar(ocorrencia);

        ocorrenciaRepository.registrarEvento(
                EventoOcorrencia.transicao(salva.id(), null, salva.estado(), null, atorId, agora));

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salva.supermercadoId(), "OCORRENCIA_ABERTA", "Ocorrencia", salva.id(),
                null, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }
}
