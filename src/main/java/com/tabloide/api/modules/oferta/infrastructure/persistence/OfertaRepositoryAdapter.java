package com.tabloide.api.modules.oferta.infrastructure.persistence;

import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OfertaRepositoryAdapter implements OfertaRepository {

    private final OfertaJpaRepository jpaRepository;

    public OfertaRepositoryAdapter(OfertaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Oferta> buscarPorIdESupermercado(Long id, Long supermercadoId) {
        return jpaRepository.findByIdAndSupermercadoId(id, supermercadoId).map(OfertaRepositoryAdapter::paraDominio);
    }

    @Override
    public List<Oferta> listarPorLojaESupermercado(Long lojaId, Long supermercadoId) {
        return jpaRepository.findByLojaIdAndSupermercadoId(lojaId, supermercadoId).stream()
                .map(OfertaRepositoryAdapter::paraDominio)
                .toList();
    }

    @Override
    public Oferta salvar(Oferta oferta) {
        OfertaJpaEntity entidade = oferta.id() == null
                ? new OfertaJpaEntity(
                        null,
                        oferta.supermercadoId(),
                        oferta.produtoId(),
                        oferta.lojaIds(),
                        oferta.precoNormal(),
                        oferta.precoPromocional(),
                        oferta.inicio(),
                        oferta.fim(),
                        oferta.condicoes(),
                        oferta.estado(),
                        null,
                        oferta.criadoEm(),
                        oferta.atualizadoEm()
                )
                : atualizar(jpaRepository.findById(oferta.id()).orElseThrow(() ->
                        new IllegalStateException("Oferta " + oferta.id() + " não encontrada para atualização")), oferta);

        // flush imediato: sem ele, o @Version em memória só é incrementado no commit da
        // transação, depois que este método já retornou a Oferta com a versão antiga para
        // o use case (e para a resposta HTTP) — mesmo motivo do PlanoRepositoryAdapter.
        return paraDominio(jpaRepository.saveAndFlush(entidade));
    }

    private static OfertaJpaEntity atualizar(OfertaJpaEntity entidade, Oferta oferta) {
        entidade.setProdutoId(oferta.produtoId());
        entidade.setLojaIds(oferta.lojaIds());
        entidade.setPrecoNormal(oferta.precoNormal());
        entidade.setPrecoPromocional(oferta.precoPromocional());
        entidade.setInicio(oferta.inicio());
        entidade.setFim(oferta.fim());
        entidade.setCondicoes(oferta.condicoes());
        entidade.setEstado(oferta.estado());
        entidade.setAtualizadoEm(oferta.atualizadoEm());
        return entidade;
    }

    private static Oferta paraDominio(OfertaJpaEntity entidade) {
        return new Oferta(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getProdutoId(),
                entidade.getLojaIds(),
                entidade.getPrecoNormal(),
                entidade.getPrecoPromocional(),
                entidade.getInicio(),
                entidade.getFim(),
                entidade.getCondicoes(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
