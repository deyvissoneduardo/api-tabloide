package com.tabloide.api.modules.produto.domain.exceptions;

public class CategoriasProdutoInvalidasException extends RuntimeException {
    public CategoriasProdutoInvalidasException() {
        super("Produto deve possuir ao menos uma categoria válida");
    }
}
