package com.tabloide.api.modules.tabloide.domain.exceptions;

public class LojasTabloideInvalidasException extends RuntimeException {
    public LojasTabloideInvalidasException() {
        super("Tabloide deve estar associado a ao menos uma loja válida");
    }
}
