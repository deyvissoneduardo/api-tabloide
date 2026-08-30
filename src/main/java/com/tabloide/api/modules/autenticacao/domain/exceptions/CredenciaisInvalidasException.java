package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos");
    }
}
