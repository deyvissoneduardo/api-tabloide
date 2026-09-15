package com.tabloide.api.modules.campanha.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import com.tabloide.api.modules.campanha.domain.exceptions.OfertaJaVinculadaAOutraCampanhaException;
import com.tabloide.api.modules.campanha.domain.exceptions.VersaoCampanhaDesatualizadaException;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.oferta.domain.exceptions.OfertaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EditarCampanha {

    private final CampanhaRepository campanhaRepository;
    private final LojaRepository lojaRepository;
    private final OfertaRepository ofertaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public EditarCampanha(
            CampanhaRepository campanhaRepository,
            LojaRepository lojaRepository,
            OfertaRepository ofertaRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.campanhaRepository = campanhaRepository;
        this.lojaRepository = lojaRepository;
        this.ofertaRepository = ofertaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Campanha executar(
            Long supermercadoId, Long campanhaId, Long versaoConhecida, DadosCampanha dados,
            Long atorId, Perfil perfilAtor, Long supermercadoIdAtor
    ) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new CampanhaNaoEncontradaException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        Campanha campanha = campanhaRepository.buscarPorIdESupermercado(campanhaId, supermercadoId)
                .orElseThrow(CampanhaNaoEncontradaException::new);

        if (!campanha.possuiVersao(versaoConhecida)) {
            throw new VersaoCampanhaDesatualizadaException();
        }

        validarLojas(dados.lojaIds(), supermercadoId);
        validarOfertas(dados.ofertaIds(), supermercadoId, campanha.id());

        String antes = campanha.resumoParaAuditoria();
        Instant agora = Instant.now();
        campanha.editar(dados.nome(), dados.descricao(), dados.lojaIds(), dados.ofertaIds(), dados.inicio(), dados.fim(), agora);
        Campanha salva = campanhaRepository.salvar(campanha);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "CAMPANHA_EDITADA", "Campanha", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private void validarLojas(Set<Long> lojaIds, Long supermercadoId) {
        for (Long lojaId : lojaIds) {
            lojaRepository.buscarPorIdESupermercado(lojaId, supermercadoId)
                    .orElseThrow(LojaNaoEncontradaException::new);
        }
    }

    private void validarOfertas(Set<Long> ofertaIds, Long supermercadoId, Long campanhaIdParaExcluir) {
        if (ofertaIds == null) {
            return;
        }
        for (Long ofertaId : ofertaIds) {
            ofertaRepository.buscarPorIdESupermercado(ofertaId, supermercadoId)
                    .orElseThrow(OfertaNaoEncontradaException::new);
            if (campanhaRepository.existeOfertaEmCampanhaAtiva(supermercadoId, ofertaId, campanhaIdParaExcluir)) {
                throw new OfertaJaVinculadaAOutraCampanhaException();
            }
        }
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
