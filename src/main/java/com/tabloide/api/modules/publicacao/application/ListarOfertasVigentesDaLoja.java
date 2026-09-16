package com.tabloide.api.modules.publicacao.application;

import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

// US-171: restringe a página pública às ofertas vigentes (RN-004), com filtro opcional por categoria.
@Component
public class ListarOfertasVigentesDaLoja {

    private final ResolverLojaPublica resolverLojaPublica;
    private final OfertaRepository ofertaRepository;
    private final ProjetorDeOfertaPublica projetorDeOfertaPublica;

    public ListarOfertasVigentesDaLoja(
            ResolverLojaPublica resolverLojaPublica, OfertaRepository ofertaRepository, ProjetorDeOfertaPublica projetorDeOfertaPublica
    ) {
        this.resolverLojaPublica = resolverLojaPublica;
        this.ofertaRepository = ofertaRepository;
        this.projetorDeOfertaPublica = projetorDeOfertaPublica;
    }

    public List<OfertaPublica> executar(Long lojaId, Long categoriaId) {
        Loja loja = resolverLojaPublica.executar(lojaId);

        return ofertaRepository.listarPorLojaESupermercado(lojaId, loja.supermercadoId()).stream()
                .map(oferta -> projetorDeOfertaPublica.projetar(oferta, loja.supermercadoId()))
                .flatMap(Optional::stream)
                .filter(ofertaPublica -> pertenceACategoriaFiltrada(ofertaPublica, categoriaId))
                .toList();
    }

    private boolean pertenceACategoriaFiltrada(OfertaPublica ofertaPublica, Long categoriaId) {
        return categoriaId == null || ofertaPublica.categoriaIds().contains(categoriaId);
    }
}
