package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException() {
        super("Perfil sem permissão para executar esta operação");
    }
}
