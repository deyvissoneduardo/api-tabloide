package com.tabloide.api.modules.categoria.interfaces.http.dto;

import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.EstadoCategoria;
import java.time.Instant;

public record CategoriaResponse(
        Long id,
        Long supermercadoId,
        String nome,
        String descricao,
        EstadoCategoria estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(
                categoria.id(),
                categoria.supermercadoId(),
                categoria.nome(),
                categoria.descricao(),
                categoria.estado(),
                categoria.versao(),
                categoria.criadoEm(),
                categoria.atualizadoEm()
        );
    }
}
