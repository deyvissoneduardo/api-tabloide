package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.Perfil;

public record DadosNovoUsuarioAdministrativo(String email, String senha, Perfil perfil) {
}
