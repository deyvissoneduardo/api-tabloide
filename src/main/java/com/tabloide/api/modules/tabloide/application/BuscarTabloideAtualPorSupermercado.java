package com.tabloide.api.modules.tabloide.application;

import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import com.tabloide.api.modules.tabloide.domain.Tabloide;
import com.tabloide.api.modules.tabloide.domain.TabloideRepository;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

// US-111: só o tabloide vigente (início <= agora <= fim) é considerado o "atual"; um
// expirado nunca é retornado aqui, sem depender de nenhum job de expiração.
@Component
public class BuscarTabloideAtualPorSupermercado {

    private final TabloideRepository tabloideRepository;

    public BuscarTabloideAtualPorSupermercado(TabloideRepository tabloideRepository) {
        this.tabloideRepository = tabloideRepository;
    }

    public Optional<Tabloide> executar(Long supermercadoId, Long supermercadoIdAtor) {
        if (!Objects.equals(supermercadoId, supermercadoIdAtor)) {
            throw new SupermercadoNaoEncontradoException();
        }
        return tabloideRepository.buscarVigentePorSupermercado(supermercadoId, Instant.now());
    }
}
