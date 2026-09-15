package com.tabloide.api.modules.campanha.domain.exceptions;

public class PeriodoCampanhaInvalidoException extends RuntimeException {
    public PeriodoCampanhaInvalidoException() {
        super("Período da campanha é inválido");
    }
}
