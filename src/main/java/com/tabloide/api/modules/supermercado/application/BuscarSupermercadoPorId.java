package com.tabloide.api.modules.supermercado.application;

import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import org.springframework.stereotype.Component;

@Component
public class BuscarSupermercadoPorId {

    private final SupermercadoRepository supermercadoRepository;

    public BuscarSupermercadoPorId(SupermercadoRepository supermercadoRepository) {
        this.supermercadoRepository = supermercadoRepository;
    }

    public Supermercado executar(Long id) {
        return supermercadoRepository.buscarPorId(id).orElseThrow(SupermercadoNaoEncontradoException::new);
    }
}
