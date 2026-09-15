package com.tabloide.api.modules.conteudogeral.application;

import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.ConteudoGeralNaoEncontradoException;
import org.springframework.stereotype.Component;

@Component
public class BuscarConteudoGeralPorId {

    private final ConteudoGeralRepository conteudoGeralRepository;

    public BuscarConteudoGeralPorId(ConteudoGeralRepository conteudoGeralRepository) {
        this.conteudoGeralRepository = conteudoGeralRepository;
    }

    public ConteudoGeral executar(Long id) {
        return conteudoGeralRepository.buscarPorId(id)
                .orElseThrow(ConteudoGeralNaoEncontradoException::new);
    }
}
