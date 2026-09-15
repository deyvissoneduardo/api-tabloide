package com.tabloide.api.modules.conteudopromocional.infrastructure.persistence;

import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocionalRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ConteudoPromocionalRepositoryAdapter implements ConteudoPromocionalRepository {

    private final ConteudoPromocionalJpaRepository jpaRepository;

    public ConteudoPromocionalRepositoryAdapter(ConteudoPromocionalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConteudoPromocional salvar(ConteudoPromocional conteudoPromocional) {
        ConteudoPromocionalJpaEntity entidade = conteudoPromocional.id() == null
                ? new ConteudoPromocionalJpaEntity(
                        null,
                        conteudoPromocional.supermercadoId(),
                        conteudoPromocional.tipo(),
                        conteudoPromocional.titulo(),
                        conteudoPromocional.texto(),
                        conteudoPromocional.nivel(),
                        conteudoPromocional.destino(),
                        conteudoPromocional.lojaIds(),
                        conteudoPromocional.inicio(),
                        conteudoPromocional.fim(),
                        conteudoPromocional.posicao(),
                        conteudoPromocional.estado(),
                        null,
                        conteudoPromocional.criadoEm(),
                        conteudoPromocional.atualizadoEm()
                )
                : atualizar(jpaRepository.findById(conteudoPromocional.id()).orElseThrow(() ->
                        new IllegalStateException("ConteudoPromocional " + conteudoPromocional.id() + " não encontrado para atualização")), conteudoPromocional);

        // flush imediato: sem ele o @Version em memória só é incrementado no commit da
        // transação, mesmo motivo documentado no OfertaRepositoryAdapter/PlanoRepositoryAdapter.
        return paraDominio(jpaRepository.saveAndFlush(entidade));
    }

    @Override
    public Optional<ConteudoPromocional> buscarPorIdESupermercado(Long id, Long supermercadoId) {
        return jpaRepository.findByIdAndSupermercadoId(id, supermercadoId).map(ConteudoPromocionalRepositoryAdapter::paraDominio);
    }

    @Override
    public List<ConteudoPromocional> listarPorSupermercadoOrdenados(Long supermercadoId) {
        return jpaRepository.findBySupermercadoIdOrderByPosicaoAsc(supermercadoId).stream()
                .map(ConteudoPromocionalRepositoryAdapter::paraDominio)
                .toList();
    }

    @Override
    public long contarPorSupermercado(Long supermercadoId) {
        return jpaRepository.countBySupermercadoId(supermercadoId);
    }

    private static ConteudoPromocionalJpaEntity atualizar(ConteudoPromocionalJpaEntity entidade, ConteudoPromocional conteudoPromocional) {
        entidade.setTitulo(conteudoPromocional.titulo());
        entidade.setTexto(conteudoPromocional.texto());
        entidade.setNivel(conteudoPromocional.nivel());
        entidade.setDestino(conteudoPromocional.destino());
        entidade.setLojaIds(conteudoPromocional.lojaIds());
        entidade.setInicio(conteudoPromocional.inicio());
        entidade.setFim(conteudoPromocional.fim());
        entidade.setPosicao(conteudoPromocional.posicao());
        entidade.setEstado(conteudoPromocional.estado());
        entidade.setAtualizadoEm(conteudoPromocional.atualizadoEm());
        return entidade;
    }

    private static ConteudoPromocional paraDominio(ConteudoPromocionalJpaEntity entidade) {
        return new ConteudoPromocional(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getTipo(),
                entidade.getTitulo(),
                entidade.getTexto(),
                entidade.getNivel(),
                entidade.getDestino(),
                entidade.getLojaIds(),
                entidade.getInicio(),
                entidade.getFim(),
                entidade.getPosicao(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
