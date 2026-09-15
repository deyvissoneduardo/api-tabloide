package com.tabloide.api.modules.tabloide.domain.exceptions;

public class ArquivoTabloideInvalidoException extends RuntimeException {
    public ArquivoTabloideInvalidoException() {
        super("Arquivo do tabloide é inválido: PDF exige URL e até 20 MB; páginas em imagem não usam URL de PDF");
    }
}
