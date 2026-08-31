package com.tabloide.api.modules.auditoria.interfaces.http.dto;

import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import java.time.Instant;

public record RegistroAuditoriaResponse(
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

    public static RegistroAuditoriaResponse from(RegistroAuditoria registro) {
        return new RegistroAuditoriaResponse(
                registro.id(),
                registro.atorId(),
                registro.perfilAtor(),
                registro.supermercadoId(),
                registro.acao(),
                registro.entidade(),
                registro.entidadeId(),
                registro.dadosAntes(),
                registro.dadosDepois(),
                registro.instante()
        );
    }
}
