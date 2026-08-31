package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.NomeDePlanoJaCadastradoException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CriarPlano {

    private final PlanoRepository planoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CriarPlano(PlanoRepository planoRepository, AuditoriaRepository auditoriaRepository) {
        this.planoRepository = planoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Plano executar(DadosPlano dados, Long atorId, Perfil perfilAtor) {
        String nomeNormalizado = Plano.normalizarNome(dados.nome());
        if (planoRepository.existeComNomeNormalizado(nomeNormalizado, null)) {
            throw new NomeDePlanoJaCadastradoException();
        }

        Instant agora = Instant.now();
        Plano plano = Plano.criar(dados.nome(), dados.validadeDias(), dados.valor(), dados.limiteFotos(), dados.limiteLojas(), agora);
        Plano salvo = planoRepository.salvar(plano);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, null, "PLANO_CRIADO", "Plano", salvo.id(),
                null, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
