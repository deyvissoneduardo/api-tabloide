package com.tabloide.api.modules.oferta.domain;

import java.util.List;
import java.util.Optional;

public interface OfertaRepository {

    Optional<Oferta> buscarPorIdESupermercado(Long id, Long supermercadoId);

    List<Oferta> listarPorLojaESupermercado(Long lojaId, Long supermercadoId);

    Oferta salvar(Oferta oferta);
}
