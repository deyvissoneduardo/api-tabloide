package com.tabloide.api.modules.supermercado.application;

import com.tabloide.api.modules.supermercado.domain.Endereco;

public record DadosSupermercado(
        String razaoSocial,
        String nomeFantasia,
        String emailComercial,
        String telefoneComercial,
        Endereco endereco,
        String complemento,
        String logomarcaUrl,
        String observacoesInternas
) {
}
