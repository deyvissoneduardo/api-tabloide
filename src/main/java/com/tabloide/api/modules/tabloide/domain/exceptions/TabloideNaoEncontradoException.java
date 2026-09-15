package com.tabloide.api.modules.tabloide.domain.exceptions;

public class TabloideNaoEncontradoException extends RuntimeException {
    public TabloideNaoEncontradoException() {
        super("Tabloide não encontrado");
    }
}
