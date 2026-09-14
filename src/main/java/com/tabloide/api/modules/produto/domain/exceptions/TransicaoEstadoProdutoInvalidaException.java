package com.tabloide.api.modules.produto.domain.exceptions;

public class TransicaoEstadoProdutoInvalidaException extends RuntimeException {

    public TransicaoEstadoProdutoInvalidaException() {
        super("Transição de estado inválida para o produto");
    }
}
