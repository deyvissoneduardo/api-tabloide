package com.tabloide.api.modules.loja.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AtivarLoja {

    private final LojaRepository lojaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public AtivarLoja(LojaRepository lojaRepository, AuditoriaRepository auditoriaRepository) {
        this.lojaRepository = lojaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Loja executar(Long supermercadoId, Long id, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new LojaNaoEncontradaException();
        }

        Loja loja = lojaRepository.buscarPorIdESupermercado(id, supermercadoId)
                .orElseThrow(LojaNaoEncontradaException::new);

        if (loja.estaAtiva()) {
            return loja;
        }

        String antes = loja.resumoParaAuditoria();
        Instant agora = Instant.now();
        loja.ativar(agora);
        Loja salva = lojaRepository.salvar(loja);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "LOJA_ATIVADA", "Loja", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
