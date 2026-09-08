package com.tabloide.api.modules.produto.domain.exceptions;

public class VersaoProdutoDesatualizadaException extends RuntimeException {
    public VersaoProdutoDesatualizadaException() {
        super("Versão do produto está desatualizada");
    }
}
