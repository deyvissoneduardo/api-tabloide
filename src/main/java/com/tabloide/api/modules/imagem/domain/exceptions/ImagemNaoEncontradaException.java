package com.tabloide.api.modules.imagem.domain.exceptions;

public class ImagemNaoEncontradaException extends RuntimeException {

    public ImagemNaoEncontradaException() {
        super("Imagem não encontrada no escopo autorizado");
    }
}
