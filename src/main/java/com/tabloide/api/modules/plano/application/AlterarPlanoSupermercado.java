package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.NenhumaAssinaturaVigenteException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AlterarPlanoSupermercado {

    private final SupermercadoRepository supermercadoRepository;
    private final PlanoRepository planoRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public AlterarPlanoSupermercado(
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

        Assinatura atual = assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(supermercadoId)
                .orElseThrow(NenhumaAssinaturaVigenteException::new);

        Plano novoPlano = planoRepository.buscarPorId(planoId)
                .orElseThrow(PlanoNaoEncontradoException::new);

        Instant agora = Instant.now();
        String antes = atual.resumoParaAuditoria();

        atual.substituir(agora);
        assinaturaRepository.salvar(atual);

        Assinatura nova = Assinatura.associar(supermercadoId, novoPlano, agora);
        Assinatura salva = assinaturaRepository.salvar(nova);

        auditoriaRepository.registrar(new RegistroAuditoria(
                atorId, perfilAtor, "PLANO_ALTERADO", "Assinatura", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }
}
