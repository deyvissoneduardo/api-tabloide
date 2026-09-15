package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class TextoConteudoPromocionalInvalidoException extends RuntimeException {
    public TextoConteudoPromocionalInvalidoException() {
        super("Texto é obrigatório para mensagem e aviso, e não se aplica a banner");
    }
}
