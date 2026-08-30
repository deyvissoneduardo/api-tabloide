package com.tabloide.api.modules.autenticacao.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SessaoTest {

    @Test
    void sessaoRecemIniciadaDeveSerValida() {
        Instant agora = Instant.now();
        Sessao sessao = Sessao.iniciar(1L, "jti-1", agora, Duration.ofHours(8));

        assertThat(sessao.estaValida(agora, Duration.ofMinutes(30))).isTrue();
    }

    @Test
    void sessaoDeveExpirarAposDuracaoMaxima() {
        Instant agora = Instant.now();
        Sessao sessao = Sessao.iniciar(1L, "jti-1", agora, Duration.ofHours(8));

        assertThat(sessao.estaExpirada(agora.plus(Duration.ofHours(9)))).isTrue();
        assertThat(sessao.estaValida(agora.plus(Duration.ofHours(9)), Duration.ofMinutes(30))).isFalse();
    }

    @Test
    void sessaoDeveInvalidarPorInatividade() {
        Instant agora = Instant.now();
        Sessao sessao = Sessao.iniciar(1L, "jti-1", agora, Duration.ofHours(8));

        Instant depoisDe31Minutos = agora.plus(Duration.ofMinutes(31));
        assertThat(sessao.estaInativaPorTempoOcioso(depoisDe31Minutos, Duration.ofMinutes(30))).isTrue();
        assertThat(sessao.estaValida(depoisDe31Minutos, Duration.ofMinutes(30))).isFalse();
    }

    @Test
    void registrarUsoDeveAtualizarJanelaDeInatividade() {
        Instant agora = Instant.now();
        Sessao sessao = Sessao.iniciar(1L, "jti-1", agora, Duration.ofHours(8));

        Instant depoisDe20Minutos = agora.plus(Duration.ofMinutes(20));
        sessao.registrarUso(depoisDe20Minutos);

        Instant depoisDeMais20Minutos = depoisDe20Minutos.plus(Duration.ofMinutes(20));
        assertThat(sessao.estaValida(depoisDeMais20Minutos, Duration.ofMinutes(30))).isTrue();
    }

    @Test
    void sessaoRevogadaNuncaDeveSerValida() {
        Instant agora = Instant.now();
        Sessao sessao = Sessao.iniciar(1L, "jti-1", agora, Duration.ofHours(8));

        sessao.revogar(agora);

        assertThat(sessao.estaRevogada()).isTrue();
        assertThat(sessao.estaValida(agora, Duration.ofMinutes(30))).isFalse();
    }
}
