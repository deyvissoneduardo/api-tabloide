package com.tabloide.api.modules.supermercado.domain.exceptions;

public class TransicaoEstadoInvalidaException extends RuntimeException {

    public TransicaoEstadoInvalidaException() {
        super("Transição de estado não permitida a partir do estado atual");
    }
}
