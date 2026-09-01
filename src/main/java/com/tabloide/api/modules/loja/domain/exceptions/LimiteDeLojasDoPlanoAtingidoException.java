package com.tabloide.api.modules.loja.domain.exceptions;

public class LimiteDeLojasDoPlanoAtingidoException extends RuntimeException {

    public LimiteDeLojasDoPlanoAtingidoException() {
        super("Limite de lojas do plano vigente foi atingido");
    }
}
