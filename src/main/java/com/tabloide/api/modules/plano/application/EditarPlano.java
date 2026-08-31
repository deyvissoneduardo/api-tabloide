package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.NomeDePlanoJaCadastradoException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoExcluidoException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import com.tabloide.api.modules.plano.domain.exceptions.VersaoDesatualizadaException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EditarPlano {

    private final PlanoRepository planoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public EditarPlano(PlanoRepository planoRepository, AuditoriaRepository auditoriaRepository) {
        this.planoRepository = planoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Plano executar(Long id, Long versaoConhecida, DadosPlano dados, Long atorId, Perfil perfilAtor) {
        Plano plano = planoRepository.buscarPorId(id)
                .orElseThrow(PlanoNaoEncontradoException::new);

        if (plano.estaExcluido()) {
            throw new PlanoExcluidoException();
        }

        if (!plano.possuiVersao(versaoConhecida)) {
            throw new VersaoDesatualizadaException();
        }

        String nomeNormalizado = Plano.normalizarNome(dados.nome());
        if (planoRepository.existeComNomeNormalizado(nomeNormalizado, plano.id())) {
            throw new NomeDePlanoJaCadastradoException();
        }

        String antes = plano.resumoParaAuditoria();
        Instant agora = Instant.now();

        plano.editar(dados.nome(), dados.validadeDias(), dados.valor(), dados.limiteFotos(), dados.limiteLojas(), agora);
        Plano salvo = planoRepository.salvar(plano);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, null, "PLANO_EDITADO", "Plano", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
