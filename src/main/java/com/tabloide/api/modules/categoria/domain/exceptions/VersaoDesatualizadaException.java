package com.tabloide.api.modules.categoria.domain.exceptions;

public class VersaoDesatualizadaException extends RuntimeException {

    public VersaoDesatualizadaException() {
        super("Versão do recurso está desatualizada");
    }
}
