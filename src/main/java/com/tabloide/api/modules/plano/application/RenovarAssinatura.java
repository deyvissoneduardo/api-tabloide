package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoExcluidoException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import com.tabloide.api.modules.plano.domain.exceptions.TransicaoEstadoInvalidaException;
import com.tabloide.api.modules.supermercado.application.AtivarSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RenovarAssinatura {

    private final SupermercadoRepository supermercadoRepository;
    private final PlanoRepository planoRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final AtivarSupermercado ativarSupermercado;
    private final AuditoriaRepository auditoriaRepository;

    public RenovarAssinatura(
            SupermercadoRepository supermercadoRepository,
            PlanoRepository planoRepository,
            AssinaturaRepository assinaturaRepository,
            AtivarSupermercado ativarSupermercado,
            AuditoriaRepository auditoriaRepository
    ) {
        this.supermercadoRepository = supermercadoRepository;
        this.planoRepository = planoRepository;
        this.assinaturaRepository = assinaturaRepository;
        this.ativarSupermercado = ativarSupermercado;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Assinatura executar(Long supermercadoId, Long atorId, Perfil perfilAtor) {
        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);

        Assinatura atual = assinaturaRepository.buscarMaisRecentePorSupermercado(supermercadoId)
                .orElseThrow(AssinaturaNaoEncontradaException::new);

        Plano plano = planoRepository.buscarPorId(atual.planoId())
                .orElseThrow(PlanoNaoEncontradoException::new);

        if (plano.estaExcluido()) {
            throw new PlanoExcluidoException();
        }

        if (atual.estaVigente()) {
            return renovarAntecipadamente(supermercado, atual, plano, atorId, perfilAtor);
        }

        if (atual.estaVencida()) {
            return renovarAposVencimento(supermercado, plano, atorId, perfilAtor);
        }

        throw new TransicaoEstadoInvalidaException();
    }

    // RN-009: assinatura ainda vigente — preserva os dias restantes e enfileira o novo período.
    private Assinatura renovarAntecipadamente(Supermercado supermercado, Assinatura atual, Plano plano, Long atorId, Perfil perfilAtor) {
        if (!supermercado.estaAtivo()) {
            throw new SupermercadoBloqueadoOuDesativadoException();
        }

        String antes = atual.resumoParaAuditoria();
        atual.renovarAntecipada(plano);
        Assinatura salva = assinaturaRepository.salvar(atual);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salva.supermercadoId(), "ASSINATURA_RENOVADA_ANTECIPADA", "Assinatura", salva.id(),
                antes, salva.resumoParaAuditoria(), Instant.now()
        ));

        return salva;
    }

    // RN-010: assinatura vencida — desbloqueia imediatamente e a nova contagem inicia no dia seguinte.
    private Assinatura renovarAposVencimento(Supermercado supermercado, Plano plano, Long atorId, Perfil perfilAtor) {
        if (!supermercado.estaBloqueado()) {
            throw new SupermercadoBloqueadoOuDesativadoException();
        }

        Instant agora = Instant.now();
        Assinatura nova = Assinatura.associar(supermercado.id(), plano, agora);
        Assinatura salva = assinaturaRepository.salvar(nova);

        ativarSupermercado.executar(supermercado.id(), atorId, perfilAtor);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salva.supermercadoId(), "ASSINATURA_RENOVADA_APOS_VENCIMENTO", "Assinatura", salva.id(),
                null, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }
}
