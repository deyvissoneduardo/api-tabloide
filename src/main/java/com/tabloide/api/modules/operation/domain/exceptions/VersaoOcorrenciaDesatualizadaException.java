package com.tabloide.api.modules.operation.domain.exceptions;

public class VersaoOcorrenciaDesatualizadaException extends RuntimeException {

    public VersaoOcorrenciaDesatualizadaException() {
        super("Versão do recurso está desatualizada");
    }
}
