package com.tabloide.api.modules.plano.domain.exceptions;

public class NenhumaAssinaturaVigenteException extends RuntimeException {

    public NenhumaAssinaturaVigenteException() {
        super("Supermercado não possui assinatura vigente para alterar; utilize a associação de plano");
    }
}
