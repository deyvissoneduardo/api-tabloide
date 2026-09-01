package com.tabloide.api.modules.loja.domain.exceptions;

public class NomeDeLojaJaCadastradoException extends RuntimeException {

    public NomeDeLojaJaCadastradoException() {
        super("Já existe uma loja com este nome neste supermercado");
    }
}
