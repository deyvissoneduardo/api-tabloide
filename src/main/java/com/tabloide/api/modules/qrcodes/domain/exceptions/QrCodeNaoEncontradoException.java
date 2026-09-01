package com.tabloide.api.modules.qrcodes.domain.exceptions;

public class QrCodeNaoEncontradoException extends RuntimeException {

    public QrCodeNaoEncontradoException() {
        super("QR Code não encontrado no escopo autorizado");
    }
}
