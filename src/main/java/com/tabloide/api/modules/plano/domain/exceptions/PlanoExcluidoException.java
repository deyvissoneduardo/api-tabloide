package com.tabloide.api.modules.plano.domain.exceptions;

public class PlanoExcluidoException extends RuntimeException {

    public PlanoExcluidoException() {
        super("Plano está excluído e não pode ser utilizado");
    }
}
