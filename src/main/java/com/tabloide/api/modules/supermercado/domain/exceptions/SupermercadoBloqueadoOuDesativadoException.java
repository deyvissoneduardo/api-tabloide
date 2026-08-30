package com.tabloide.api.modules.supermercado.domain.exceptions;

public class SupermercadoBloqueadoOuDesativadoException extends RuntimeException {

    public SupermercadoBloqueadoOuDesativadoException() {
        super("Supermercado bloqueado ou desativado não permite esta alteração");
    }
}
