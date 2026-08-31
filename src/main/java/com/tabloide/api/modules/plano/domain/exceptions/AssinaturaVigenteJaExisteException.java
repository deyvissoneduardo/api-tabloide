package com.tabloide.api.modules.plano.domain.exceptions;

public class AssinaturaVigenteJaExisteException extends RuntimeException {

    public AssinaturaVigenteJaExisteException() {
        super("Supermercado já possui uma assinatura vigente ou agendada; utilize a alteração de plano");
    }
}
