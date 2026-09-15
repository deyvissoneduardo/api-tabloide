package com.tabloide.api.modules.tabloide.domain;

import java.time.Instant;
import java.util.Optional;

public interface TabloideRepository {

    Tabloide salvar(Tabloide tabloide);

    Optional<Tabloide> buscarVigentePorSupermercado(Long supermercadoId, Instant agora);
}
