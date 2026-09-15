package com.tabloide.api.modules.campanha.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CancelarCampanha {

    private final CampanhaRepository campanhaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CancelarCampanha(CampanhaRepository campanhaRepository, AuditoriaRepository auditoriaRepository) {
        this.campanhaRepository = campanhaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Campanha executar(Long supermercadoId, Long campanhaId, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new CampanhaNaoEncontradaException();
        }

        Campanha campanha = campanhaRepository.buscarPorIdESupermercado(campanhaId, supermercadoId)
                .orElseThrow(CampanhaNaoEncontradaException::new);

        if (campanha.estaFinalizada()) {
            return campanha;
        }

        String antes = campanha.resumoParaAuditoria();
        Instant agora = Instant.now();
        campanha.cancelar(agora);
        Campanha salva = campanhaRepository.salvar(campanha);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "CAMPANHA_CANCELADA", "Campanha", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
