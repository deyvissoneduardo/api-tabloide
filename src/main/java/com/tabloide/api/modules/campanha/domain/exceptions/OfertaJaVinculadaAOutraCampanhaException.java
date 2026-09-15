package com.tabloide.api.modules.campanha.domain.exceptions;

public class OfertaJaVinculadaAOutraCampanhaException extends RuntimeException {
    public OfertaJaVinculadaAOutraCampanhaException() {
        super("A oferta já está vinculada a outra campanha ativa");
    }
}
