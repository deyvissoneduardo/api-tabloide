package com.tabloide.api.modules.imagem.domain.exceptions;

public class CotaDeImagensExcedidaException extends RuntimeException {

    public CotaDeImagensExcedidaException() {
        super("Cota de imagens do plano vigente foi atingida");
    }
}
