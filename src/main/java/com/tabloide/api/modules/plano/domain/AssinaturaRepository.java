package com.tabloide.api.modules.plano.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AssinaturaRepository {

    Optional<Assinatura> buscarVigenteOuAgendadaPorSupermercado(Long supermercadoId);

    Optional<Assinatura> buscarMaisRecentePorSupermercado(Long supermercadoId);

    Assinatura salvar(Assinatura assinatura);

    List<Assinatura> listarVigentesComVencimentoAte(Instant limite);

    List<Assinatura> listarVigentesComVencimentoEntre(Instant inicio, Instant fim);
}
