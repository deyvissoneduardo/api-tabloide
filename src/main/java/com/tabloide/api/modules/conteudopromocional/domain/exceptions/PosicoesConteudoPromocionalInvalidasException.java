package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class PosicoesConteudoPromocionalInvalidasException extends RuntimeException {
    public PosicoesConteudoPromocionalInvalidasException() {
        super("A reordenação deve incluir, exatamente uma vez cada, todos os conteúdos promocionais do supermercado");
    }
}
