package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class LojasConteudoPromocionalInvalidasException extends RuntimeException {
    public LojasConteudoPromocionalInvalidasException() {
        super("Conteúdo promocional deve estar associado a ao menos uma loja válida");
    }
}
