package com.tabloide.api.modules.produto.domain.exceptions;

public class CategoriaDesativadaNaoAceitaAssociacaoException extends RuntimeException {

    public CategoriaDesativadaNaoAceitaAssociacaoException() {
        super("Categoria desativada não aceita novas associações de produto");
    }
}
