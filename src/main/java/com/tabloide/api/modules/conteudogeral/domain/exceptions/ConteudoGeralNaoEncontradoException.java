package com.tabloide.api.modules.conteudogeral.domain.exceptions;

public class ConteudoGeralNaoEncontradoException extends RuntimeException {
    public ConteudoGeralNaoEncontradoException() {
        super("Conteúdo geral não encontrado");
    }
}
