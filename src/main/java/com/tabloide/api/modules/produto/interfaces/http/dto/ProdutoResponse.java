package com.tabloide.api.modules.produto.interfaces.http.dto;

import com.tabloide.api.modules.produto.domain.EstadoProduto;
import com.tabloide.api.modules.produto.domain.Produto;
import java.time.Instant;
import java.util.Set;

public record ProdutoResponse(
        Long id,
        Long supermercadoId,
        String nome,
        Set<Long> categoriaIds,
        String marca,
        String descricao,
        String peso,
        String unidade,
        String volume,
        EstadoProduto estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static ProdutoResponse from(Produto produto) {
        return new ProdutoResponse(
                produto.id(),
                produto.supermercadoId(),
                produto.nome(),
                produto.categoriaIds(),
                produto.marca(),
                produto.descricao(),
                produto.peso(),
                produto.unidade(),
                produto.volume(),
                produto.estado(),
                produto.versao(),
                produto.criadoEm(),
                produto.atualizadoEm()
        );
    }
}
