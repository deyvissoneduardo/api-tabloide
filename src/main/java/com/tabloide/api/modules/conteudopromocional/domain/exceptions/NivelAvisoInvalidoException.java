package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class NivelAvisoInvalidoException extends RuntimeException {
    public NivelAvisoInvalidoException() {
        super("Nível informativo é obrigatório para aviso, e não se aplica a mensagem ou banner");
    }
}
