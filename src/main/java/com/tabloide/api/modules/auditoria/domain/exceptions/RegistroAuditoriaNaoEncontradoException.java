package com.tabloide.api.modules.auditoria.domain.exceptions;

public class RegistroAuditoriaNaoEncontradoException extends RuntimeException {

    public RegistroAuditoriaNaoEncontradoException() {
        super("Registro de auditoria não encontrado");
    }
}
