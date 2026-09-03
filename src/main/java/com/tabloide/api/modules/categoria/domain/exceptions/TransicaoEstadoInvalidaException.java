package com.tabloide.api.modules.categoria.domain.exceptions;

public class TransicaoEstadoInvalidaException extends RuntimeException {

    public TransicaoEstadoInvalidaException() {
        super("Transição de estado inválida para a categoria");
    }
}
