package com.tabloide.api.modules.autenticacao.infrastructure.security;

import com.tabloide.api.modules.autenticacao.domain.Perfil;

public interface RegistradorDeAuditoriaDeLeitura {

    void registrarConsulta(Long atorId, Perfil perfilAtor, Long supermercadoId, String acao, String entidade, Long entidadeId);
}
