package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class DadosRedefinicaoNaoConferemException extends RuntimeException {

    public DadosRedefinicaoNaoConferemException() {
        super("CNPJ e e-mail informados não conferem com um usuário cadastrado");
    }
}
