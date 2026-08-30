package com.tabloide.api.modules.autenticacao.infrastructure.security;

import java.util.Optional;

public final class ContextoAutenticacao {

    private static final ThreadLocal<ClaimsSessao> ATUAL = new ThreadLocal<>();

    private ContextoAutenticacao() {
    }

    public static void definir(ClaimsSessao claims) {
        ATUAL.set(claims);
    }

    public static Optional<ClaimsSessao> atual() {
        return Optional.ofNullable(ATUAL.get());
    }

    public static void limpar() {
        ATUAL.remove();
    }
}
