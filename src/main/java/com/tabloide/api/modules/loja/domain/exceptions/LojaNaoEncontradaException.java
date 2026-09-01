package com.tabloide.api.modules.loja.domain.exceptions;

public class LojaNaoEncontradaException extends RuntimeException {

    public LojaNaoEncontradaException() {
        super("Loja não encontrada no escopo autorizado");
    }
}
