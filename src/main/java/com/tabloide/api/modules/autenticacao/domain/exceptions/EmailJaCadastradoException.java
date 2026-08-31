package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("E-mail já cadastrado na plataforma");
    }
}
