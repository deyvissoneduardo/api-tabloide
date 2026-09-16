package com.tabloide.api.modules.qrcodes.domain.exceptions;

// RN-005 (QR Codes): QR Code desativado, ou de loja desativada, não redireciona ao conteúdo.
public class QrCodeIndisponivelException extends RuntimeException {

    public QrCodeIndisponivelException() {
        super("QR Code indisponível");
    }
}
