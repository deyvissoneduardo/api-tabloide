package com.tabloide.api.modules.auditoria.domain;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import java.time.Instant;

public record RegistroAuditoria(
        Long atorId,
        Perfil perfilAtor,
        String acao,
        String entidade,
        Long entidadeId,
        String dadosAntes,
        String dadosDepois,
        Instant instante
) {
}
