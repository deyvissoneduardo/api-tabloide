package com.tabloide.api.modules.supermercado.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarSupermercados {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final SupermercadoRepository supermercadoRepository;

    public ListarSupermercados(SupermercadoRepository supermercadoRepository) {
        this.supermercadoRepository = supermercadoRepository;
    }

    public Pagina<Supermercado> executar(int pagina, int tamanho) {
        validarPaginacao(pagina, tamanho);
        return supermercadoRepository.listar(pagina, tamanho);
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
    }
}
