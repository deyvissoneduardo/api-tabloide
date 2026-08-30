package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class TamanhoPaginaInvalidoException extends RuntimeException {

    public TamanhoPaginaInvalidoException() {
        super("Página deve ser >= 0 e tamanho deve ser 25, 50 ou 100");
    }
}
