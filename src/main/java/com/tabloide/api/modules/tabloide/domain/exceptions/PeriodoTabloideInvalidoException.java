package com.tabloide.api.modules.tabloide.domain.exceptions;

public class PeriodoTabloideInvalidoException extends RuntimeException {
    public PeriodoTabloideInvalidoException() {
        super("Data de início do tabloide não pode ser posterior à data de fim");
    }
}
