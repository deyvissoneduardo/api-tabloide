package com.tabloide.api.modules.produto.domain;

import java.util.Optional;

public interface ProdutoRepository {

    Optional<Produto> buscarPorIdESupermercado(Long id, Long supermercadoId);

    Produto salvar(Produto produto);
}
