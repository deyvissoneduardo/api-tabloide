package com.tabloide.api.modules.tabloide.domain.exceptions;

public class TituloTabloideInvalidoException extends RuntimeException {
    public TituloTabloideInvalidoException() {
        super("Título do tabloide é obrigatório");
    }
}
