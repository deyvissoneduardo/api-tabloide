package com.tabloide.api.modules.oferta.domain.exceptions;

public class OfertaNaoEncontradaException extends RuntimeException {
    public OfertaNaoEncontradaException() {
        super("Oferta não encontrada");
    }
}
