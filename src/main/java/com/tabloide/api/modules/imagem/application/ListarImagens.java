package com.tabloide.api.modules.imagem.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.ImagemRepository;
import com.tabloide.api.modules.imagem.domain.exceptions.ImagemNaoEncontradaException;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarImagens {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final ImagemRepository imagemRepository;

    public ListarImagens(ImagemRepository imagemRepository) {
        this.imagemRepository = imagemRepository;
    }

    // RN-005: biblioteca isolada por supermercado; Super Admin consulta qualquer supermercado para suporte.
    public Pagina<Imagem> executar(
            Long supermercadoId, Perfil perfilAtor, Long supermercadoIdAtor, String nomeBusca, int pagina, int tamanho) {
        if (perfilAtor != Perfil.SUPER_ADMIN && !Objects.equals(supermercadoId, supermercadoIdAtor)) {
            throw new ImagemNaoEncontradaException();
        }
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
        return imagemRepository.listarPorSupermercado(supermercadoId, nomeBusca, pagina, tamanho);
    }
}
