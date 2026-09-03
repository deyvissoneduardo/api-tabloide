package com.tabloide.api.modules.categoria.domain.exceptions;

public class CategoriaNaoEncontradaException extends RuntimeException {

    public CategoriaNaoEncontradaException() {
        super("Categoria não encontrada no escopo autorizado");
    }
}
