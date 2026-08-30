package com.tabloide.api.modules.autenticacao.domain;

import java.time.Duration;
import java.time.Instant;

public class RedefinicaoSenha {

    private final Long id;
    private final Long usuarioId;
    private final String tokenHash;
    private final Instant criadoEm;
    private final Instant expiraEm;
    private Instant usadoEm;

    public RedefinicaoSenha(
            Long id,
            Long usuarioId,
            String tokenHash,
            Instant criadoEm,
            Instant expiraEm,
            Instant usadoEm
    ) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
        this.usadoEm = usadoEm;
    }

    public static RedefinicaoSenha solicitar(Long usuarioId, String tokenHash, Instant agora, Duration validade) {
        return new RedefinicaoSenha(null, usuarioId, tokenHash, agora, agora.plus(validade), null);
    }

    public boolean estaValida(Instant agora) {
        return usadoEm == null && agora.isBefore(expiraEm);
    }

    public void marcarComoUsado(Instant agora) {
        this.usadoEm = agora;
    }

    public Long id() {
        return id;
    }

    public Long usuarioId() {
        return usuarioId;
    }

    public String tokenHash() {
        return tokenHash;
    }

    public Instant criadoEm() {
        return criadoEm;
    }

    public Instant expiraEm() {
        return expiraEm;
    }

    public Instant usadoEm() {
        return usadoEm;
    }
}
