package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado no escopo autorizado");
    }
}
