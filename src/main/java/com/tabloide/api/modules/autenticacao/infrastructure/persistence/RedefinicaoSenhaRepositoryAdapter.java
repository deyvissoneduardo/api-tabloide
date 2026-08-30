package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenha;
import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenhaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RedefinicaoSenhaRepositoryAdapter implements RedefinicaoSenhaRepository {

    private final RedefinicaoSenhaJpaRepository jpaRepository;

    public RedefinicaoSenhaRepositoryAdapter(RedefinicaoSenhaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(RedefinicaoSenha redefinicaoSenha) {
        RedefinicaoSenhaJpaEntity entidade = redefinicaoSenha.id() == null
                ? new RedefinicaoSenhaJpaEntity(
                        null,
                        redefinicaoSenha.usuarioId(),
                        redefinicaoSenha.tokenHash(),
                        redefinicaoSenha.criadoEm(),
                        redefinicaoSenha.expiraEm(),
                        redefinicaoSenha.usadoEm()
                )
                : jpaRepository.findById(redefinicaoSenha.id())
                        .orElseThrow(() -> new IllegalStateException("Redefinição " + redefinicaoSenha.id() + " não encontrada para atualização"));

        entidade.setUsadoEm(redefinicaoSenha.usadoEm());
        jpaRepository.save(entidade);
    }

    @Override
    public Optional<RedefinicaoSenha> buscarPorTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(RedefinicaoSenhaRepositoryAdapter::paraDominio);
    }

    private static RedefinicaoSenha paraDominio(RedefinicaoSenhaJpaEntity entidade) {
        return new RedefinicaoSenha(
                entidade.getId(),
                entidade.getUsuarioId(),
                entidade.getTokenHash(),
                entidade.getCriadoEm(),
                entidade.getExpiraEm(),
                entidade.getUsadoEm()
        );
    }
}
