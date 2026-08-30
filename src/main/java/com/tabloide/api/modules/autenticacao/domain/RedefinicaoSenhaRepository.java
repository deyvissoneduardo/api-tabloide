package com.tabloide.api.modules.autenticacao.domain;

import java.util.Optional;

public interface RedefinicaoSenhaRepository {

    void salvar(RedefinicaoSenha redefinicaoSenha);

    Optional<RedefinicaoSenha> buscarPorTokenHash(String tokenHash);
}
