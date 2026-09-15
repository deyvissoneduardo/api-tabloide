package com.tabloide.api.modules.conteudogeral.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.ConteudoGeralNaoEncontradoException;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.VersaoConteudoGeralDesatualizadaException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PublicarConteudoGeral {

    private final ConteudoGeralRepository conteudoGeralRepository;
    private final AuditoriaRepository auditoriaRepository;

    public PublicarConteudoGeral(ConteudoGeralRepository conteudoGeralRepository, AuditoriaRepository auditoriaRepository) {
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

        Instant agora = Instant.now();
        arquivarPublicadoAnterior(conteudoGeral, atorId, perfilAtor, agora);

        String antes = conteudoGeral.resumoParaAuditoria();
        conteudoGeral.publicar(agora);
        ConteudoGeral salvo = conteudoGeralRepository.salvar(conteudoGeral);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, null, "CONTEUDO_GERAL_PUBLICADO", "ConteudoGeral", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }

    private void arquivarPublicadoAnterior(ConteudoGeral conteudoGeral, Long atorId, Perfil perfilAtor, Instant agora) {
        Optional<ConteudoGeral> publicadoAnterior = conteudoGeralRepository.buscarPublicadoPorTipo(conteudoGeral.tipo())
                .filter(anterior -> !Objects.equals(anterior.id(), conteudoGeral.id()));

        if (publicadoAnterior.isEmpty()) {
            return;
        }

        ConteudoGeral anterior = publicadoAnterior.get();
        String antes = anterior.resumoParaAuditoria();
        anterior.arquivar(agora);
        ConteudoGeral arquivado = conteudoGeralRepository.salvar(anterior);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, null, "CONTEUDO_GERAL_ARQUIVADO", "ConteudoGeral", arquivado.id(),
                antes, arquivado.resumoParaAuditoria(), agora
        ));
    }
}
