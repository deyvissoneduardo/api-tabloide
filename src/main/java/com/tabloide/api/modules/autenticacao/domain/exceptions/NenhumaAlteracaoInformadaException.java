package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class NenhumaAlteracaoInformadaException extends RuntimeException {

    public NenhumaAlteracaoInformadaException() {
        super("Informe um novo e-mail ou uma nova senha para alterar seus dados");
    }
}
