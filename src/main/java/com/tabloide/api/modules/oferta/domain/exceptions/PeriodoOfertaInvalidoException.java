package com.tabloide.api.modules.oferta.domain.exceptions;

public class PeriodoOfertaInvalidoException extends RuntimeException {
    public PeriodoOfertaInvalidoException() {
        super("Data de início da oferta não pode ser posterior à data de fim");
    }
}
