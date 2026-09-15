package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class TituloConteudoPromocionalInvalidoException extends RuntimeException {
    public TituloConteudoPromocionalInvalidoException() {
        super("Título do conteúdo promocional é obrigatório");
    }
}
