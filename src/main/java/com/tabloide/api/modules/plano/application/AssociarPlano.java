package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaVigenteJaExisteException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AssociarPlano {

    private final SupermercadoRepository supermercadoRepository;
    private final PlanoRepository planoRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public AssociarPlano(
            SupermercadoRepository supermercadoRepository,
            PlanoRepository planoRepository,
            AssinaturaRepository assinaturaRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.supermercadoRepository = supermercadoRepository;
        this.planoRepository = planoRepository;
        this.assinaturaRepository = assinaturaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Assinatura executar(Long supermercadoId, Long planoId, Long atorId, Perfil perfilAtor) {
        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);

        if (!supermercado.estaAtivo()) {
            throw new SupermercadoBloqueadoOuDesativadoException();
        }

        if (assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(supermercadoId).isPresent()) {
            throw new AssinaturaVigenteJaExisteException();
        }

        Plano plano = planoRepository.buscarPorId(planoId)
                .orElseThrow(PlanoNaoEncontradoException::new);

        Instant agora = Instant.now();
        Assinatura assinatura = Assinatura.associar(supermercadoId, plano, agora);
        Assinatura salva = assinaturaRepository.salvar(assinatura);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salva.supermercadoId(), "PLANO_ASSOCIADO", "Assinatura", salva.id(),
                null, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }
}
