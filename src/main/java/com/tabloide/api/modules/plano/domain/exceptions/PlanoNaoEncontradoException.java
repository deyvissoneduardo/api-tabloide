package com.tabloide.api.modules.plano.domain.exceptions;

public class PlanoNaoEncontradoException extends RuntimeException {

    public PlanoNaoEncontradoException() {
        super("Plano não encontrado");
    }
}
