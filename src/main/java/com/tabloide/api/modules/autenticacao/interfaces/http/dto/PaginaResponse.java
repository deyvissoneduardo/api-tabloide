package com.tabloide.api.modules.autenticacao.interfaces.http.dto;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.List;
import java.util.function.Function;

public record PaginaResponse<T>(List<T> itens, int pagina, int tamanho, long totalItens, int totalPaginas) {

    public static <D, T> PaginaResponse<T> from(Pagina<D> pagina, Function<D, T> mapeador) {
        return new PaginaResponse<>(
                pagina.itens().stream().map(mapeador).toList(),
                pagina.pagina(),
                pagina.tamanho(),
                pagina.totalItens(),
                pagina.totalPaginas()
        );
    }
}
