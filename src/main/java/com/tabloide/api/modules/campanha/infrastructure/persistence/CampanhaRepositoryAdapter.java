package com.tabloide.api.modules.campanha.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class CampanhaRepositoryAdapter implements CampanhaRepository {

    private final CampanhaJpaRepository jpaRepository;

    public CampanhaRepositoryAdapter(CampanhaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Campanha> buscarPorIdESupermercado(Long id, Long supermercadoId) {
        return jpaRepository.findByIdAndSupermercadoId(id, supermercadoId).map(CampanhaRepositoryAdapter::paraDominio);
    }

    @Override
    public List<Campanha> buscarPorIdsESupermercado(Set<Long> ids, Long supermercadoId) {
        return jpaRepository.findByIdInAndSupermercadoId(ids, supermercadoId).stream()
                .map(CampanhaRepositoryAdapter::paraDominio)
                .toList();
    }

    @Override
    public Campanha salvar(Campanha campanha) {
        CampanhaJpaEntity entidade = campanha.id() == null
                ? new CampanhaJpaEntity(
                        null,
                        campanha.supermercadoId(),
                        campanha.nome(),
                        campanha.descricao(),
                        campanha.lojaIds(),
                        campanha.ofertaIds(),
                        campanha.inicio(),
                        campanha.fim(),
                        campanha.estado(),
                        null,
                        campanha.criadoEm(),
                        campanha.atualizadoEm()
                )
                : atualizar(jpaRepository.findById(campanha.id()).orElseThrow(() ->
                        new IllegalStateException("Campanha " + campanha.id() + " não encontrada para atualização")), campanha);

        // flush imediato: sem ele, o @Version em memória só é incrementado no commit da
        // transação, depois que este método já retornou a Campanha com a versão antiga para
        // o use case (e para a resposta HTTP) — mesmo motivo do PlanoRepositoryAdapter.
        return paraDominio(jpaRepository.saveAndFlush(entidade));
    }

    @Override
    public Pagina<Campanha> listarPorSupermercado(Long supermercadoId, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "criadoEm"));
        Page<CampanhaJpaEntity> resultado = jpaRepository.findBySupermercadoId(supermercadoId, paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(CampanhaRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    @Override
    public boolean existeOfertaEmCampanhaAtiva(Long supermercadoId, Long ofertaId, Long campanhaIdParaExcluir) {
        return jpaRepository.existeOfertaEmCampanhaAtiva(supermercadoId, ofertaId, campanhaIdParaExcluir);
    }

    private static CampanhaJpaEntity atualizar(CampanhaJpaEntity entidade, Campanha campanha) {
        entidade.setNome(campanha.nome());
        entidade.setDescricao(campanha.descricao());
        entidade.setLojaIds(campanha.lojaIds());
        entidade.setOfertaIds(campanha.ofertaIds());
        entidade.setInicio(campanha.inicio());
        entidade.setFim(campanha.fim());
        entidade.setEstado(campanha.estado());
        entidade.setAtualizadoEm(campanha.atualizadoEm());
        return entidade;
    }

    private static Campanha paraDominio(CampanhaJpaEntity entidade) {
        return new Campanha(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getNome(),
                entidade.getDescricao(),
                entidade.getLojaIds(),
                entidade.getOfertaIds(),
                entidade.getInicio(),
                entidade.getFim(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
