package com.tabloide.api.modules.campanha.domain.exceptions;

public class TransicaoEstadoCampanhaInvalidaException extends RuntimeException {
    public TransicaoEstadoCampanhaInvalidaException() {
        super("Transição de estado inválida para a campanha");
    }
}
