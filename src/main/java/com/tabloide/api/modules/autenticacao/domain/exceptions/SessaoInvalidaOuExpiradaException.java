package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class SessaoInvalidaOuExpiradaException extends RuntimeException {

    public SessaoInvalidaOuExpiradaException() {
        super("Sessão ausente, inválida ou expirada");
    }
}
