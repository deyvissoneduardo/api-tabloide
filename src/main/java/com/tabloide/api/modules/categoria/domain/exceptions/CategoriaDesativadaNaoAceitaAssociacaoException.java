package com.tabloide.api.modules.categoria.domain.exceptions;

public class CategoriaDesativadaNaoAceitaAssociacaoException extends RuntimeException {

    public CategoriaDesativadaNaoAceitaAssociacaoException() {
        super("Categoria desativada não aceita novas associações de produto");
    }
}
