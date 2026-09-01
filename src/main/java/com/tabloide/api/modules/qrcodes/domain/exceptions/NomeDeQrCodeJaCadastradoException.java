package com.tabloide.api.modules.qrcodes.domain.exceptions;

public class NomeDeQrCodeJaCadastradoException extends RuntimeException {

    public NomeDeQrCodeJaCadastradoException() {
        super("Já existe um QR Code com este nome neste supermercado");
    }
}
