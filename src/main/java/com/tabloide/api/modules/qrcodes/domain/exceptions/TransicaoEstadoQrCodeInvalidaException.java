package com.tabloide.api.modules.qrcodes.domain.exceptions;

public class TransicaoEstadoQrCodeInvalidaException extends RuntimeException {

    public TransicaoEstadoQrCodeInvalidaException() {
        super("Transição de estado inválida para o QR Code");
    }
}
