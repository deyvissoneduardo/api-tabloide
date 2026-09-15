package com.tabloide.api.modules.conteudopromocional.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocionalRepository;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.PosicoesConteudoPromocionalInvalidasException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// US-116: recebe a ordem final (todos os ids do supermercado, uma vez cada) e regrava
// posição = índice na lista — evita, por construção, qualquer posição duplicada (RN-007).
@Component
public class ReordenarConteudosPromocionais {

    private final ConteudoPromocionalRepository conteudoPromocionalRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public ReordenarConteudosPromocionais(
            ConteudoPromocionalRepository conteudoPromocionalRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.conteudoPromocionalRepository = conteudoPromocionalRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public List<ConteudoPromocional> executar(Long supermercadoId, List<Long> idsEmOrdem, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new SupermercadoNaoEncontradoException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        List<ConteudoPromocional> existentes = conteudoPromocionalRepository.listarPorSupermercadoOrdenados(supermercadoId);
        Map<Long, ConteudoPromocional> porId = existentes.stream().collect(Collectors.toMap(ConteudoPromocional::id, Function.identity()));
        validarConjuntoCompleto(idsEmOrdem, porId.keySet());

        Instant agora = Instant.now();
        List<ConteudoPromocional> reordenados = new ArrayList<>();
        for (int posicao = 0; posicao < idsEmOrdem.size(); posicao++) {
            ConteudoPromocional conteudo = porId.get(idsEmOrdem.get(posicao));
            conteudo.atribuirPosicao(posicao, agora);
            reordenados.add(conteudoPromocionalRepository.salvar(conteudo));
        }

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "CONTEUDOS_PROMOCIONAIS_REORDENADOS", "ConteudoPromocional", null,
                null, "novaOrdem=" + idsEmOrdem, agora
        ));

        return reordenados;
    }

    private void validarConjuntoCompleto(List<Long> idsEmOrdem, Set<Long> idsExistentes) {
        Set<Long> informados = new HashSet<>(idsEmOrdem);
        boolean temDuplicado = informados.size() != idsEmOrdem.size();
        if (temDuplicado || !informados.equals(idsExistentes)) {
            throw new PosicoesConteudoPromocionalInvalidasException();
        }
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
