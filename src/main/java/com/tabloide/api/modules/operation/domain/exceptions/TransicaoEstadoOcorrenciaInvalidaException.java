package com.tabloide.api.modules.operation.domain.exceptions;

public class TransicaoEstadoOcorrenciaInvalidaException extends RuntimeException {

    public TransicaoEstadoOcorrenciaInvalidaException() {
        super("Transição de estado não permitida a partir do estado atual");
    }
}
