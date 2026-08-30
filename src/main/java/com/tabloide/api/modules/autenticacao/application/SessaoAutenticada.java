package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.Sessao;
import com.tabloide.api.modules.autenticacao.domain.Usuario;

public record SessaoAutenticada(Usuario usuario, Sessao sessao) {
}
