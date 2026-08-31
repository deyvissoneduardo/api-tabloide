package com.tabloide.api.modules.plano.domain;

public interface AvisoInternoRepository {

    /**
     * Grava o aviso se ainda não existir um igual (mesma assinatura + tipo).
     *
     * @return true se um novo aviso foi gravado; false se já existia (idempotência do job de vencimento).
     */
    boolean salvarSeNaoExiste(AvisoInterno aviso);
}
