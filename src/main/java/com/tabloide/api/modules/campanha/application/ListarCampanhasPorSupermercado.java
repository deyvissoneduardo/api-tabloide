package com.tabloide.api.modules.campanha.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarCampanhasPorSupermercado {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final CampanhaRepository campanhaRepository;

    public ListarCampanhasPorSupermercado(CampanhaRepository campanhaRepository) {
        this.campanhaRepository = campanhaRepository;
    }

    public Pagina<Campanha> executar(Long supermercadoId, Long supermercadoIdAtor, int pagina, int tamanho) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new CampanhaNaoEncontradaException();
        }
        validarPaginacao(pagina, tamanho);
        return campanhaRepository.listarPorSupermercado(supermercadoId, pagina, tamanho);
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
