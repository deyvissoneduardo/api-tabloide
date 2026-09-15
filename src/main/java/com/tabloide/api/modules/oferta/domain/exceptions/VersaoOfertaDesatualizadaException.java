package com.tabloide.api.modules.oferta.domain.exceptions;

public class VersaoOfertaDesatualizadaException extends RuntimeException {
    public VersaoOfertaDesatualizadaException() {
        super("Versão da oferta está desatualizada");
    }
}
