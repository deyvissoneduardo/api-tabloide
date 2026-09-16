package com.tabloide.api.modules.publicacao.application;

import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.CategoriaRepository;
import com.tabloide.api.modules.oferta.domain.EstadoOferta;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

// RN-004 (Página pública): mostra somente ofertas VIGENTES cujo produto e ao menos uma categoria
// estejam ativos. Loja/supermercado já são validados por ResolverLojaPublica antes de chegar aqui.
@Component
class ProjetorDeOfertaPublica {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    ProjetorDeOfertaPublica(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    Optional<OfertaPublica> projetar(Oferta oferta, Long supermercadoId) {
        if (!estaVigente(oferta)) {
            return Optional.empty();
        }
        Produto produto = produtoRepository.buscarPorIdESupermercado(oferta.produtoId(), supermercadoId).orElse(null);
        if (produto == null || !produto.estaAtivo()) {
            return Optional.empty();
        }
        List<Categoria> categoriasAtivas = categoriaRepository.listarPorIdsESupermercado(produto.categoriaIds(), supermercadoId).stream()
                .filter(Categoria::estaAtiva)
                .toList();
        if (categoriasAtivas.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(OfertaPublica.de(oferta, produto, categoriasAtivas));
    }

    private boolean estaVigente(Oferta oferta) {
        return oferta.estadoEfetivo(Instant.now()) == EstadoOferta.VIGENTE;
    }
}
