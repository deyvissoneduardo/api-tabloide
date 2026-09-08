package com.tabloide.api.modules.oferta.domain.exceptions;

public class PrecoOfertaInvalidoException extends RuntimeException {
    public PrecoOfertaInvalidoException() {
        super("Preço promocional deve ser positivo, com duas casas decimais, e menor que o preço normal");
    }
}
