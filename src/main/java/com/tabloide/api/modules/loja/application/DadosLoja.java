package com.tabloide.api.modules.loja.application;

import com.tabloide.api.modules.supermercado.domain.Endereco;

public record DadosLoja(
        String nome,
        Endereco endereco,
        String complemento
) {
}
