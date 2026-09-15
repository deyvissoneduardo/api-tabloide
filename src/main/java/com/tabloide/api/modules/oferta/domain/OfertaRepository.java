package com.tabloide.api.modules.oferta.domain;

import java.util.Optional;

public interface OfertaRepository {

    Optional<Oferta> buscarPorIdESupermercado(Long id, Long supermercadoId);

    Oferta salvar(Oferta oferta);
}
