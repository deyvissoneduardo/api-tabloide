package com.tabloide.api.modules.publicacao.application;

import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.produto.domain.Produto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// RN-008/RN-011 (Página pública): somente os campos públicos da oferta — nada de dados administrativos,
// de estoque interno ou de identificação do consumidor.
public record OfertaPublica(
        Long ofertaId,
        Long produtoId,
        String nomeProduto,
        String marca,
        String descricao,
        Set<Long> categoriaIds,
        BigDecimal precoNormal,
        BigDecimal precoPromocional,
        Integer percentualDesconto,
        Instant validadeAte,
        String condicoes
) {

    static OfertaPublica de(Oferta oferta, Produto produto, List<Categoria> categoriasAtivas) {
        return new OfertaPublica(
                oferta.id(),
                produto.id(),
                produto.nome(),
                produto.marca(),
                produto.descricao(),
                categoriasAtivas.stream().map(Categoria::id).collect(Collectors.toSet()),
                oferta.precoNormal(),
                oferta.precoPromocional(),
                oferta.percentualDesconto(),
                oferta.fim(),
                oferta.condicoes()
        );
    }
}
