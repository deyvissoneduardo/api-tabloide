package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarPlanos {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final PlanoRepository planoRepository;

    public ListarPlanos(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    public Pagina<Plano> executar(int pagina, int tamanho) {
        validarPaginacao(pagina, tamanho);
        return planoRepository.listar(pagina, tamanho);
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
    }
}
