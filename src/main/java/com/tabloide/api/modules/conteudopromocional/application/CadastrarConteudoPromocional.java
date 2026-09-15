package com.tabloide.api.modules.conteudopromocional.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocionalRepository;
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

// Cobre US-112 (mensagens), US-113 (avisos) e US-114 (banners): os três tipos compartilham o
// mesmo cadastro, com o tipo determinando quais campos são exigidos (ver ConteudoPromocional).
@Component
public class CadastrarConteudoPromocional {

    private final ConteudoPromocionalRepository conteudoPromocionalRepository;
    private final LojaRepository lojaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CadastrarConteudoPromocional(
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
    public ConteudoPromocional executar(Long supermercadoId, DadosConteudoPromocional dados, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new SupermercadoNaoEncontradoException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        validarLojas(dados.lojaIds(), supermercadoId);

        // RN-007: posição é sempre atribuída ao final da lista existente, o que garante,
        // por construção, que nunca existam duas posições iguais para o mesmo supermercado.
        int proximaPosicao = (int) conteudoPromocionalRepository.contarPorSupermercado(supermercadoId);

        Instant agora = Instant.now();
        ConteudoPromocional conteudo = ConteudoPromocional.cadastrar(
                supermercadoId, dados.tipo(), dados.titulo(), dados.texto(), dados.nivel(), dados.destino(),
                dados.lojaIds(), dados.inicio(), dados.fim(), proximaPosicao, agora
        );
        ConteudoPromocional salvo = conteudoPromocionalRepository.salvar(conteudo);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "CONTEUDO_PROMOCIONAL_CADASTRADO", "ConteudoPromocional", salvo.id(),
                null, salvo.resumoParaAuditoria(), agora
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
