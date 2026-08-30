package com.tabloide.api.modules.supermercado.domain.exceptions;

public class SupermercadoNaoEncontradoException extends RuntimeException {

    public SupermercadoNaoEncontradoException() {
        super("Supermercado não encontrado");
    }
}
