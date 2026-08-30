package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Sessao;
import com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SessaoRepositoryAdapter implements SessaoRepository {

    private final SessaoJpaRepository jpaRepository;

    public SessaoRepositoryAdapter(SessaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(Sessao sessao) {
        SessaoJpaEntity entidade = sessao.id() == null
                ? new SessaoJpaEntity(null, sessao.usuarioId(), sessao.jti(), sessao.criadoEm(), sessao.expiraEm(), sessao.ultimoUsoEm(), sessao.revogadaEm())
                : jpaRepository.findById(sessao.id())
                        .orElseThrow(() -> new IllegalStateException("Sessão " + sessao.id() + " não encontrada para atualização"));

        entidade.setUltimoUsoEm(sessao.ultimoUsoEm());
        entidade.setRevogadaEm(sessao.revogadaEm());
        jpaRepository.save(entidade);
    }

    @Override
    public Optional<Sessao> buscarPorJti(String jti) {
        return jpaRepository.findByJti(jti).map(SessaoRepositoryAdapter::paraDominio);
    }

    @Override
    @Transactional
    public void revogarTodasDoUsuario(Long usuarioId, Instant agora) {
        jpaRepository.revogarTodasDoUsuario(usuarioId, agora);
    }

    @Override
    public Pagina<SessaoDetalhada> listar(Long supermercadoId, int pagina, int tamanho) {
        Page<SessaoDetalhada> resultado = jpaRepository.listar(supermercadoId, PageRequest.of(pagina, tamanho));
        return new Pagina<>(resultado.getContent(), pagina, tamanho, resultado.getTotalElements(), resultado.getTotalPages());
    }

    @Override
    public Optional<SessaoDetalhada> buscarDetalhePorJti(String jti) {
        return jpaRepository.buscarDetalhePorJti(jti);
    }

    private static Sessao paraDominio(SessaoJpaEntity entidade) {
        return new Sessao(
                entidade.getId(),
                entidade.getUsuarioId(),
                entidade.getJti(),
                entidade.getCriadoEm(),
                entidade.getExpiraEm(),
                entidade.getUltimoUsoEm(),
                entidade.getRevogadaEm()
        );
    }
}
