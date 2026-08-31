package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import org.springframework.stereotype.Component;

@Component
public class BuscarAssinaturaVigente {

    private final AssinaturaRepository assinaturaRepository;

    public BuscarAssinaturaVigente(AssinaturaRepository assinaturaRepository) {
        this.assinaturaRepository = assinaturaRepository;
    }

    public Assinatura executar(Long supermercadoId) {
        return assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(supermercadoId)
                .orElseThrow(AssinaturaNaoEncontradaException::new);
    }
}
