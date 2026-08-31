package com.tabloide.api.modules.operation.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.operation.domain.FiltroOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarOcorrencias {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final OcorrenciaRepository ocorrenciaRepository;

    public ListarOcorrencias(OcorrenciaRepository ocorrenciaRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
    }

    public Pagina<Ocorrencia> executar(FiltroOcorrencia filtro, int pagina, int tamanho) {
        validarPaginacao(pagina, tamanho);
        return ocorrenciaRepository.listar(filtro, pagina, tamanho);
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
    }
}
