package com.tabloide.api.modules.conteudogeral.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CriarConteudoGeral {

    private final ConteudoGeralRepository conteudoGeralRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CriarConteudoGeral(ConteudoGeralRepository conteudoGeralRepository, AuditoriaRepository auditoriaRepository) {
        this.conteudoGeralRepository = conteudoGeralRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public ConteudoGeral executar(DadosConteudoGeral dados, Long atorId, Perfil perfilAtor) {
        Instant agora = Instant.now();
        ConteudoGeral conteudoGeral = ConteudoGeral.criar(dados.tipo(), dados.titulo(), dados.corpo(), agora);
        ConteudoGeral salvo = conteudoGeralRepository.salvar(conteudoGeral);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, null, "CONTEUDO_GERAL_CADASTRADO", "ConteudoGeral", salvo.id(),
                null, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
