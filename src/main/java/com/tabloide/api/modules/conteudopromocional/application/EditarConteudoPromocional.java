package com.tabloide.api.modules.conteudopromocional.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocionalRepository;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.ConteudoPromocionalNaoEncontradoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.VersaoConteudoPromocionalDesatualizadaException;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Cobre US-115 (definir a validade dos conteúdos): validade é apenas um dos campos editáveis
// aqui, junto de título, texto/nível/destino (conforme o tipo) e lojas.
@Component
public class EditarConteudoPromocional {

    private final ConteudoPromocionalRepository conteudoPromocionalRepository;
    private final LojaRepository lojaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public EditarConteudoPromocional(
            ConteudoPromocionalRepository conteudoPromocionalRepository,
            LojaRepository lojaRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.conteudoPromocionalRepository = conteudoPromocionalRepository;
        this.lojaRepository = lojaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public ConteudoPromocional executar(
            Long supermercadoId, Long conteudoId, Long versaoConhecida, DadosConteudoPromocional dados,
            Long atorId, Perfil perfilAtor, Long supermercadoIdAtor
    ) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new ConteudoPromocionalNaoEncontradoException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        ConteudoPromocional conteudo = conteudoPromocionalRepository.buscarPorIdESupermercado(conteudoId, supermercadoId)
                .orElseThrow(ConteudoPromocionalNaoEncontradoException::new);

        if (!conteudo.possuiVersao(versaoConhecida)) {
            throw new VersaoConteudoPromocionalDesatualizadaException();
        }

        validarLojas(dados.lojaIds(), supermercadoId);

        String antes = conteudo.resumoParaAuditoria();
        Instant agora = Instant.now();
        conteudo.editar(dados.titulo(), dados.texto(), dados.nivel(), dados.destino(), dados.lojaIds(), dados.inicio(), dados.fim(), agora);
        ConteudoPromocional salvo = conteudoPromocionalRepository.salvar(conteudo);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "CONTEUDO_PROMOCIONAL_EDITADO", "ConteudoPromocional", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }

    private void validarLojas(Set<Long> lojaIds, Long supermercadoId) {
        for (Long lojaId : lojaIds) {
            lojaRepository.buscarPorIdESupermercado(lojaId, supermercadoId)
                    .orElseThrow(LojaNaoEncontradaException::new);
        }
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
