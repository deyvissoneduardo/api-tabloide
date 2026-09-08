package com.tabloide.api.modules.oferta.domain.exceptions;

public class LojasOfertaInvalidasException extends RuntimeException {
    public LojasOfertaInvalidasException() {
        super("Oferta deve estar associada a ao menos uma loja válida");
    }
}
