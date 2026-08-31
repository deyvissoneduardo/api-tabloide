package com.tabloide.api.modules.plano.domain.exceptions;

public class AssinaturaNaoEncontradaException extends RuntimeException {

    public AssinaturaNaoEncontradaException() {
        super("Nenhuma assinatura vigente ou agendada encontrada para o supermercado");
    }
}
