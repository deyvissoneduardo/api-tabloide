package com.tabloide.api.modules.campanha.application;

import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class BuscarCampanhaPorId {

    private final CampanhaRepository campanhaRepository;

    public BuscarCampanhaPorId(CampanhaRepository campanhaRepository) {
        this.campanhaRepository = campanhaRepository;
    }

    public Campanha executar(Long supermercadoId, Long campanhaId, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new CampanhaNaoEncontradaException();
        }
        return campanhaRepository.buscarPorIdESupermercado(campanhaId, supermercadoId)
                .orElseThrow(CampanhaNaoEncontradaException::new);
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
