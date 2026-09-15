package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class VersaoConteudoPromocionalDesatualizadaException extends RuntimeException {
    public VersaoConteudoPromocionalDesatualizadaException() {
        super("Versão do conteúdo promocional está desatualizada");
    }
}
