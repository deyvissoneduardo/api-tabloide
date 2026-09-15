package com.tabloide.api.modules.conteudogeral.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ConteudoGeralRepositoryAdapter implements ConteudoGeralRepository {

    private final ConteudoGeralJpaRepository jpaRepository;

    public ConteudoGeralRepositoryAdapter(ConteudoGeralJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ConteudoGeral> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ConteudoGeralRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<ConteudoGeral> buscarPublicadoPorTipo(TipoConteudoGeral tipo) {
        return jpaRepository.findByTipoAndEstado(tipo, EstadoConteudoGeral.PUBLICADO).map(ConteudoGeralRepositoryAdapter::paraDominio);
    }

    @Override
    public ConteudoGeral salvar(ConteudoGeral conteudoGeral) {
        ConteudoGeralJpaEntity entidade = conteudoGeral.id() == null
                ? new ConteudoGeralJpaEntity(
                        null,
                        conteudoGeral.tipo(),
                        conteudoGeral.titulo(),
                        conteudoGeral.corpo(),
                        conteudoGeral.estado(),
                        null,
                        conteudoGeral.criadoEm(),
                        conteudoGeral.atualizadoEm(),
                        conteudoGeral.publicadoEm()
                )
                : atualizar(jpaRepository.findById(conteudoGeral.id()).orElseThrow(() ->
                        new IllegalStateException("ConteudoGeral " + conteudoGeral.id() + " não encontrado para atualização")), conteudoGeral);

        // flush imediato: sem ele, o @Version em memória só é incrementado no commit da
        // transação, depois que este método já retornou o ConteudoGeral com a versão antiga
        // para o use case (e para a resposta HTTP) — mesmo motivo do PlanoRepositoryAdapter.
        return paraDominio(jpaRepository.saveAndFlush(entidade));
    }

    @Override
    public Pagina<ConteudoGeral> listar(TipoConteudoGeral tipo, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "criadoEm"));
        Page<ConteudoGeralJpaEntity> resultado = tipo == null
                ? jpaRepository.findAll(paginacao)
                : jpaRepository.findByTipo(tipo, paginacao);

        return new Pagina<>(
                resultado.getContent().stream().map(ConteudoGeralRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private static ConteudoGeralJpaEntity atualizar(ConteudoGeralJpaEntity entidade, ConteudoGeral conteudoGeral) {
        entidade.setTitulo(conteudoGeral.titulo());
        entidade.setCorpo(conteudoGeral.corpo());
        entidade.setEstado(conteudoGeral.estado());
        entidade.setAtualizadoEm(conteudoGeral.atualizadoEm());
        entidade.setPublicadoEm(conteudoGeral.publicadoEm());
        return entidade;
    }

    private static ConteudoGeral paraDominio(ConteudoGeralJpaEntity entidade) {
        return new ConteudoGeral(
                entidade.getId(),
                entidade.getTipo(),
                entidade.getTitulo(),
                entidade.getCorpo(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm(),
                entidade.getPublicadoEm()
        );
    }
}
