package com.tabloide.api.modules.categoria.domain;

import java.util.Optional;

public interface CategoriaRepository {

    Optional<Categoria> buscarPorIdESupermercado(Long id, Long supermercadoId);

    Categoria salvar(Categoria categoria);
}
