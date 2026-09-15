package com.tabloide.api.modules.conteudogeral.domain.exceptions;

public class VersaoConteudoGeralDesatualizadaException extends RuntimeException {
    public VersaoConteudoGeralDesatualizadaException() {
        super("Versão do conteúdo geral está desatualizada");
    }
}
