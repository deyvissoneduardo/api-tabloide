package com.tabloide.api.modules.autenticacao.domain;

import java.time.Duration;
import java.time.Instant;

public class Sessao {

    private final Long id;
    private final Long usuarioId;
    private final String jti;
    private final Instant criadoEm;
    private final Instant expiraEm;
    private Instant ultimoUsoEm;
    private Instant revogadaEm;

    public Sessao(
            Long id,
            Long usuarioId,
            String jti,
            Instant criadoEm,
            Instant expiraEm,
            Instant ultimoUsoEm,
            Instant revogadaEm
    ) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.jti = jti;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
        this.ultimoUsoEm = ultimoUsoEm;
        this.revogadaEm = revogadaEm;
    }

    public static Sessao iniciar(Long usuarioId, String jti, Instant agora, Duration duracaoMaxima) {
        return new Sessao(null, usuarioId, jti, agora, agora.plus(duracaoMaxima), agora, null);
    }

    public boolean estaRevogada() {
        return revogadaEm != null;
    }

    public boolean estaExpirada(Instant agora) {
        return agora.isAfter(expiraEm);
    }

    public boolean estaInativaPorTempoOcioso(Instant agora, Duration tempoInatividade) {
        return Duration.between(ultimoUsoEm, agora).compareTo(tempoInatividade) > 0;
    }

    public boolean estaValida(Instant agora, Duration tempoInatividade) {
        return !estaRevogada() && !estaExpirada(agora) && !estaInativaPorTempoOcioso(agora, tempoInatividade);
    }

    public void registrarUso(Instant agora) {
        this.ultimoUsoEm = agora;
    }

    public void revogar(Instant agora) {
        this.revogadaEm = agora;
    }

    public Long id() {
        return id;
    }

    public Long usuarioId() {
        return usuarioId;
    }

    public String jti() {
        return jti;
    }

    public Instant criadoEm() {
        return criadoEm;
    }

    public Instant expiraEm() {
        return expiraEm;
    }

    public Instant ultimoUsoEm() {
        return ultimoUsoEm;
    }

    public Instant revogadaEm() {
        return revogadaEm;
    }
}
