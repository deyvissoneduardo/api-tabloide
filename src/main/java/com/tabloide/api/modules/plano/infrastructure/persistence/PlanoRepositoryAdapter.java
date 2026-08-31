package com.tabloide.api.modules.plano.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class PlanoRepositoryAdapter implements PlanoRepository {

    private final PlanoJpaRepository jpaRepository;

    public PlanoRepositoryAdapter(PlanoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Plano> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(PlanoRepositoryAdapter::paraDominio);
    }

    @Override
    public Plano salvar(Plano plano) {
        PlanoJpaEntity entidade = plano.id() == null
                ? new PlanoJpaEntity(
                        null,
                        plano.nome(),
                        plano.nomeNormalizado(),
                        plano.validadeDias(),
                        plano.valor(),
                        plano.limiteFotos(),
                        plano.limiteLojas(),
                        null,
                        plano.criadoEm(),
                        plano.atualizadoEm(),
                        plano.excluidoEm()
                )
                : atualizar(jpaRepository.findById(plano.id()).orElseThrow(() ->
                        new IllegalStateException("Plano " + plano.id() + " não encontrado para atualização")), plano);

        // flush imediato: sem ele, o @Version em memória só é incrementado no commit da
        // transação, depois que este método já retornou o Plano com a versão antiga para o
        // use case (e para a resposta HTTP).
        return paraDominio(jpaRepository.saveAndFlush(entidade));
    }

    @Override
    public boolean existeComNomeNormalizado(String nomeNormalizado, Long idParaExcluir) {
        return idParaExcluir == null
                ? jpaRepository.existsByNomeNormalizadoAndExcluidoEmIsNull(nomeNormalizado)
                : jpaRepository.existsByNomeNormalizadoAndIdNotAndExcluidoEmIsNull(nomeNormalizado, idParaExcluir);
    }

    @Override
    public Pagina<Plano> listar(int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.ASC, "nome"));
        Page<PlanoJpaEntity> resultado = jpaRepository.findAll(paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(PlanoRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private static PlanoJpaEntity atualizar(PlanoJpaEntity entidade, Plano plano) {
        entidade.setNome(plano.nome());
        entidade.setNomeNormalizado(plano.nomeNormalizado());
        entidade.setValidadeDias(plano.validadeDias());
        entidade.setValor(plano.valor());
        entidade.setLimiteFotos(plano.limiteFotos());
        entidade.setLimiteLojas(plano.limiteLojas());
        entidade.setAtualizadoEm(plano.atualizadoEm());
        entidade.setExcluidoEm(plano.excluidoEm());
        return entidade;
    }

    private static Plano paraDominio(PlanoJpaEntity entidade) {
        return new Plano(
                entidade.getId(),
                entidade.getNome(),
                entidade.getNomeNormalizado(),
                entidade.getValidadeDias(),
                entidade.getValor(),
                entidade.getLimiteFotos(),
                entidade.getLimiteLojas(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm(),
                entidade.getExcluidoEm()
        );
    }
}
