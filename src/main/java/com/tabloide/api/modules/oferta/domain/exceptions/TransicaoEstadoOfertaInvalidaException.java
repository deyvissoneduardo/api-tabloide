package com.tabloide.api.modules.oferta.domain.exceptions;

public class TransicaoEstadoOfertaInvalidaException extends RuntimeException {
    public TransicaoEstadoOfertaInvalidaException() {
        super("Transição de estado inválida para a oferta");
    }
}
