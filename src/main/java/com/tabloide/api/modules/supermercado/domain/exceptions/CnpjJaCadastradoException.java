package com.tabloide.api.modules.supermercado.domain.exceptions;

public class CnpjJaCadastradoException extends RuntimeException {

    public CnpjJaCadastradoException() {
        super("CNPJ já cadastrado na plataforma");
    }
}
