package com.tabloide.api.modules.auditoria.domain;

import java.time.Instant;

public record FiltroAuditoria(Long atorId, String acao, String entidade, Instant dataInicio, Instant dataFim, Long supermercadoId) {
}
