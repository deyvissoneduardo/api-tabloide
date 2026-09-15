package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class PeriodoConteudoPromocionalInvalidoException extends RuntimeException {
    public PeriodoConteudoPromocionalInvalidoException() {
        super("Data de início do conteúdo promocional não pode ser posterior à data de fim");
    }
}
