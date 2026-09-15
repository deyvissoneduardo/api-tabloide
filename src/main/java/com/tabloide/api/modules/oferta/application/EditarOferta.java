package com.tabloide.api.modules.oferta.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.oferta.domain.exceptions.OfertaNaoEncontradaException;
import com.tabloide.api.modules.oferta.domain.exceptions.VersaoOfertaDesatualizadaException;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.produto.domain.exceptions.ProdutoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EditarOferta {

    private final OfertaRepository ofertaRepository;
    private final ProdutoRepository produtoRepository;
    private final LojaRepository lojaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public EditarOferta(
            OfertaRepository ofertaRepository,
            ProdutoRepository produtoRepository,
            LojaRepository lojaRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.ofertaRepository = ofertaRepository;
        this.produtoRepository = produtoRepository;
        this.lojaRepository = lojaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Oferta executar(
            Long supermercadoId, Long ofertaId, Long versaoConhecida, DadosOferta dados,
            Long atorId, Perfil perfilAtor, Long supermercadoIdAtor
    ) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new OfertaNaoEncontradaException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        Oferta oferta = ofertaRepository.buscarPorIdESupermercado(ofertaId, supermercadoId)
                .orElseThrow(OfertaNaoEncontradaException::new);

        if (!oferta.possuiVersao(versaoConhecida)) {
            throw new VersaoOfertaDesatualizadaException();
        }

        validarProduto(dados.produtoId(), supermercadoId);
        validarLojas(dados.lojaIds(), supermercadoId);

        String antes = oferta.resumoParaAuditoria();
        Instant agora = Instant.now();
        oferta.editar(
                dados.produtoId(), dados.lojaIds(), dados.precoNormal(), dados.precoPromocional(),
                dados.inicio(), dados.fim(), dados.condicoes(), agora
        );
        Oferta salva = ofertaRepository.salvar(oferta);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "OFERTA_EDITADA", "Oferta", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private void validarProduto(Long produtoId, Long supermercadoId) {
        produtoRepository.buscarPorIdESupermercado(produtoId, supermercadoId)
                .orElseThrow(ProdutoNaoEncontradoException::new);
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
