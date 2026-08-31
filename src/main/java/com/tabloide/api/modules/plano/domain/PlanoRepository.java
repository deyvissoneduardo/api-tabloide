package com.tabloide.api.modules.plano.domain;

import java.util.Optional;

public interface PlanoRepository {

    Optional<Plano> buscarPorId(Long id);
}
