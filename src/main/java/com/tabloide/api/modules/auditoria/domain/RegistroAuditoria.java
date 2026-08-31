package com.tabloide.api.modules.auditoria.domain;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import java.time.Instant;

public record RegistroAuditoria(
        Long id,
        Long atorId,
        Perfil perfilAtor,
        Long supermercadoId,
        String acao,
        String entidade,
        Long entidadeId,
        String dadosAntes,
        String dadosDepois,
        Instant instante
) {

    public static RegistroAuditoria deConsulta(
            Long atorId, Perfil perfilAtor, Long supermercadoId, String acao, String entidade, Long entidadeId, Instant instante) {
        return new RegistroAuditoria(null, atorId, perfilAtor, supermercadoId, acao, entidade, entidadeId, null, null, instante);
    }
}
