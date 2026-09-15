package com.tabloide.api.modules.conteudopromocional.domain.exceptions;

public class DestinoBannerInvalidoException extends RuntimeException {
    public DestinoBannerInvalidoException() {
        super("Destino só se aplica a banner e deve ser um caminho interno (iniciado por /) ou URL HTTPS");
    }
}
