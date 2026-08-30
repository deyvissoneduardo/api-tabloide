package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class TokenRedefinicaoInvalidoOuExpiradoException extends RuntimeException {

    public TokenRedefinicaoInvalidoOuExpiradoException() {
        super("Token de redefinição de senha inválido, já utilizado ou expirado");
    }
}
