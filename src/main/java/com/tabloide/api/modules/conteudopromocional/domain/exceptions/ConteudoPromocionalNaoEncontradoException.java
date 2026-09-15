package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class ConteudoPromocionalNaoEncontradoException extends RuntimeException {
    public ConteudoPromocionalNaoEncontradoException() {
        super("Conteúdo promocional não encontrado");
    }
}
