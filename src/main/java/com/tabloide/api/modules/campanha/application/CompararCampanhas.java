package com.tabloide.api.modules.campanha.application;

import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CompararCampanhas {

    private final CampanhaRepository campanhaRepository;

    public CompararCampanhas(CampanhaRepository campanhaRepository) {
        this.campanhaRepository = campanhaRepository;
    }

    public List<Campanha> executar(Long supermercadoId, Set<Long> campanhaIds, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new CampanhaNaoEncontradaException();
        }
        return campanhaRepository.buscarPorIdsESupermercado(campanhaIds, supermercadoId);
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
