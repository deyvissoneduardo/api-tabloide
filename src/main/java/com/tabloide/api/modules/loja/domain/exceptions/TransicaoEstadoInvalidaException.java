package com.tabloide.api.modules.loja.domain.exceptions;

public class TransicaoEstadoInvalidaException extends RuntimeException {

    public TransicaoEstadoInvalidaException() {
        super("Transição de estado inválida para a loja");
    }
}
