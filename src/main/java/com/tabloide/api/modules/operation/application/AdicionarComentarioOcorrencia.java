package com.tabloide.api.modules.operation.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.operation.domain.EventoOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
import com.tabloide.api.modules.operation.domain.exceptions.OcorrenciaNaoEncontradaException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdicionarComentarioOcorrencia {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public AdicionarComentarioOcorrencia(OcorrenciaRepository ocorrenciaRepository, AuditoriaRepository auditoriaRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Ocorrencia executar(Long id, String comentario, Long atorId, Perfil perfilAtor) {
        Ocorrencia ocorrencia = ocorrenciaRepository.buscarPorId(id).orElseThrow(OcorrenciaNaoEncontradaException::new);

        Instant agora = Instant.now();
        ocorrencia.registrarAtividade(agora);
        Ocorrencia salva = ocorrenciaRepository.salvar(ocorrencia);

        ocorrenciaRepository.registrarEvento(EventoOcorrencia.comentario(salva.id(), comentario, atorId, agora));

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salva.supermercadoId(), "OCORRENCIA_COMENTADA", "Ocorrencia", salva.id(),
                null, comentario, agora
        ));

        return salva;
    }
}
