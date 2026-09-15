package com.tabloide.api.modules.conteudogeral.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarConteudosGerais {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final ConteudoGeralRepository conteudoGeralRepository;

    public ListarConteudosGerais(ConteudoGeralRepository conteudoGeralRepository) {
        this.conteudoGeralRepository = conteudoGeralRepository;
    }

    public Pagina<ConteudoGeral> executar(TipoConteudoGeral tipo, int pagina, int tamanho) {
        validarPaginacao(pagina, tamanho);
        return conteudoGeralRepository.listar(tipo, pagina, tamanho);
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
    }
}
