package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import org.springframework.stereotype.Component;

@Component
public class BuscarPlanoPorId {

    private final PlanoRepository planoRepository;

    public BuscarPlanoPorId(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    public Plano executar(Long id) {
        return planoRepository.buscarPorId(id)
                .orElseThrow(PlanoNaoEncontradoException::new);
    }
}
