package com.tabloide.api.modules.produto.application;

import java.util.Set;

public record DadosProduto(
        String nome,
        Set<Long> categoriaIds,
        String marca,
        String descricao,
        String peso,
        String unidade,
        String volume
) {
}
