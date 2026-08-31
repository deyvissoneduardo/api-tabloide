package com.tabloide.api.modules.plano.domain.exceptions;

public class TransicaoEstadoInvalidaException extends RuntimeException {

    public TransicaoEstadoInvalidaException() {
        super("Assinatura não está em um estado que permita esta operação");
    }
}
