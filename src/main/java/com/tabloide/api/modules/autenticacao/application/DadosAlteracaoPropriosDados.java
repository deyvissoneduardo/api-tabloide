package com.tabloide.api.modules.autenticacao.application;

public record DadosAlteracaoPropriosDados(
        String senhaAtual,
        String novoEmail,
        String novaSenha
) {
}
