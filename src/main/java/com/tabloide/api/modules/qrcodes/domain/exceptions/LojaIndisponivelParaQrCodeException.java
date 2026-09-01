package com.tabloide.api.modules.qrcodes.domain.exceptions;

public class LojaIndisponivelParaQrCodeException extends RuntimeException {

    public LojaIndisponivelParaQrCodeException() {
        super("A loja informada está desativada e não pode receber um novo QR Code");
    }
}
