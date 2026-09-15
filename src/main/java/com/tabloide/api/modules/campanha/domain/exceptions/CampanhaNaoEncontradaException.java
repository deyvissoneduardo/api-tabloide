package com.tabloide.api.modules.campanha.domain.exceptions;

public class CampanhaNaoEncontradaException extends RuntimeException {
    public CampanhaNaoEncontradaException() {
        super("Campanha não encontrada");
    }
}
