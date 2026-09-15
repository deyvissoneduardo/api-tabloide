package com.tabloide.api.modules.oferta.infrastructure.persistence;

import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
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
    public Oferta salvar(Oferta oferta) {
        OfertaJpaEntity entidade = new OfertaJpaEntity(
                oferta.id(),
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
        );
        return paraDominio(jpaRepository.save(entidade));
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
