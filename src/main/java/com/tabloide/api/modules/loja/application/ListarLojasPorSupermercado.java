package com.tabloide.api.modules.loja.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarLojasPorSupermercado {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final LojaRepository lojaRepository;

    public ListarLojasPorSupermercado(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    public Pagina<Loja> executar(Long supermercadoId, Perfil perfilAtor, Long supermercadoIdAtor, int pagina, int tamanho) {
        if (foraDoEscopoDoAtor(supermercadoId, perfilAtor, supermercadoIdAtor)) {
            throw new LojaNaoEncontradaException();
        }
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
        return lojaRepository.listarPorSupermercado(supermercadoId, pagina, tamanho);
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (perfilAtor == Perfil.SUPER_ADMIN) {
            return false;
        }
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
