package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException() {
        super("Sessão não encontrada no escopo autorizado");
    }
}
