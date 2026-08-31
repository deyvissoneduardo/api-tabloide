package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExcluirPlano {

    private final PlanoRepository planoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public ExcluirPlano(PlanoRepository planoRepository, AuditoriaRepository auditoriaRepository) {
        this.planoRepository = planoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Plano executar(Long id, Long atorId, Perfil perfilAtor) {
        Plano plano = planoRepository.buscarPorId(id)
                .orElseThrow(PlanoNaoEncontradoException::new);

        if (plano.estaExcluido()) {
            return plano;
        }

        String antes = plano.resumoParaAuditoria();
        Instant agora = Instant.now();

        plano.excluirLogicamente(agora);
        Plano salvo = planoRepository.salvar(plano);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, null, "PLANO_EXCLUIDO", "Plano", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
