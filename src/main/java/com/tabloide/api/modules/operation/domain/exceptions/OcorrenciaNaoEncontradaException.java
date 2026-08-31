package com.tabloide.api.modules.operation.domain.exceptions;

public class OcorrenciaNaoEncontradaException extends RuntimeException {

    public OcorrenciaNaoEncontradaException() {
        super("Ocorrência não encontrada");
    }
}
