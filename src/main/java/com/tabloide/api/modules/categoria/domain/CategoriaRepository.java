package com.tabloide.api.modules.categoria.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoriaRepository {

    Optional<Categoria> buscarPorIdESupermercado(Long id, Long supermercadoId);

    List<Categoria> listarPorIdsESupermercado(Set<Long> ids, Long supermercadoId);

    Categoria salvar(Categoria categoria);
}
