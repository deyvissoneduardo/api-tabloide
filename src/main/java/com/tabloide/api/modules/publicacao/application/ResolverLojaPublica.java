package com.tabloide.api.modules.publicacao.application;

import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.publicacao.domain.exceptions.RecursoPublicoIndisponivelException;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import org.springframework.stereotype.Component;

// RN-004 (Página pública): só é possível consultar loja ativa de supermercado não desativado.
@Component
class ResolverLojaPublica {

    private final LojaRepository lojaRepository;
    private final SupermercadoRepository supermercadoRepository;

    ResolverLojaPublica(LojaRepository lojaRepository, SupermercadoRepository supermercadoRepository) {
        this.lojaRepository = lojaRepository;
        this.supermercadoRepository = supermercadoRepository;
    }

    Loja executar(Long lojaId) {
        Loja loja = lojaRepository.buscarPorId(lojaId)
                .filter(Loja::estaAtiva)
                .orElseThrow(RecursoPublicoIndisponivelException::new);
        supermercadoRepository.buscarPorId(loja.supermercadoId())
                .filter(supermercado -> !supermercado.estaDesativado())
                .orElseThrow(RecursoPublicoIndisponivelException::new);
        return loja;
    }
}
