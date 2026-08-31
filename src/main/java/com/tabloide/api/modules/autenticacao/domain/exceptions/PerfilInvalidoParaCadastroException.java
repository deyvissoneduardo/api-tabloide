package com.tabloide.api.modules.autenticacao.domain.exceptions;

public class PerfilInvalidoParaCadastroException extends RuntimeException {

    public PerfilInvalidoParaCadastroException() {
        super("Perfil informado não pode ser cadastrado via API");
    }
}
