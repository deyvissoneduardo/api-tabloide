package com.tabloide.api.modules.campanha.domain.exceptions;

public class VersaoCampanhaDesatualizadaException extends RuntimeException {
    public VersaoCampanhaDesatualizadaException() {
        super("Versão da campanha está desatualizada");
    }
}
