package com.tabloide.api.modules.conteudogeral.domain.exceptions;

public class TransicaoEstadoConteudoGeralInvalidaException extends RuntimeException {
    public TransicaoEstadoConteudoGeralInvalidaException() {
        super("Transição de estado inválida para o conteúdo geral");
    }
}
