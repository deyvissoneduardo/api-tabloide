package com.tabloide.api.modules.plano.domain;

import java.util.Optional;

public interface AssinaturaRepository {

    Optional<Assinatura> buscarVigenteOuAgendadaPorSupermercado(Long supermercadoId);

    Assinatura salvar(Assinatura assinatura);
}
