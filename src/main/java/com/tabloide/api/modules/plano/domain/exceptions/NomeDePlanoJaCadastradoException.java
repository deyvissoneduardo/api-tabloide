package com.tabloide.api.modules.plano.domain.exceptions;

public class NomeDePlanoJaCadastradoException extends RuntimeException {

    public NomeDePlanoJaCadastradoException() {
        super("Já existe um plano disponível com esse nome");
    }
}
