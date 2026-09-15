package com.tabloide.api.modules.conteudogeral.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.ConteudoGeralNaoEncontradoException;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.VersaoConteudoGeralDesatualizadaException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArquivarConteudoGeral {

    private final ConteudoGeralRepository conteudoGeralRepository;
    private final AuditoriaRepository auditoriaRepository;

    public ArquivarConteudoGeral(ConteudoGeralRepository conteudoGeralRepository, AuditoriaRepository auditoriaRepository) {
        this.conteudoGeralRepository = conteudoGeralRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public ConteudoGeral executar(Long id, Long versaoConhecida, Long atorId, Perfil perfilAtor) {
        ConteudoGeral conteudoGeral = conteudoGeralRepository.buscarPorId(id)
                .orElseThrow(ConteudoGeralNaoEncontradoException::new);

        if (!conteudoGeral.possuiVersao(versaoConhecida)) {
            throw new VersaoConteudoGeralDesatualizadaException();
        }

        String antes = conteudoGeral.resumoParaAuditoria();
        Instant agora = Instant.now();
        conteudoGeral.arquivar(agora);
        ConteudoGeral salvo = conteudoGeralRepository.salvar(conteudoGeral);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, null, "CONTEUDO_GERAL_ARQUIVADO", "ConteudoGeral", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
