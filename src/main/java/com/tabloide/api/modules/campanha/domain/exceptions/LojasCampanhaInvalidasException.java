package com.tabloide.api.modules.campanha.domain.exceptions;

public class LojasCampanhaInvalidasException extends RuntimeException {
    public LojasCampanhaInvalidasException() {
        super("A campanha deve ter ao menos uma loja válida");
    }
}
