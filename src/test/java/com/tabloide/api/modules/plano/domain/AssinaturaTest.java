package com.tabloide.api.modules.plano.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class AssinaturaTest {

    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");

    @Test
    void deveIniciarNoDiaSeguinteETerminarNoUltimoDiaDaValidade() {
        Plano plano = new Plano(1L, "Básico", 7, BigDecimal.ZERO, 100);
        Instant agora = ZonedDateTime.of(2026, 8, 30, 15, 0, 0, 0, FUSO_BRASILIA).toInstant();

        Assinatura assinatura = Assinatura.associar(10L, plano, agora);

        Instant inicioEsperado = ZonedDateTime.of(2026, 8, 31, 0, 0, 0, 0, FUSO_BRASILIA).toInstant();
        Instant fimEsperado = ZonedDateTime.of(2026, 9, 6, 23, 59, 59, 0, FUSO_BRASILIA).toInstant();
        assertThat(assinatura.dataInicio()).isEqualTo(inicioEsperado);
        assertThat(assinatura.dataFim()).isEqualTo(fimEsperado);
        assertThat(assinatura.estado()).isEqualTo(EstadoAssinatura.VIGENTE);
        assertThat(assinatura.planoNome()).isEqualTo("Básico");
    }

    @Test
    void deveSubstituirEEncerrarNoInstanteDaTroca() {
        Plano plano = new Plano(1L, "Básico", 7, BigDecimal.ZERO, 100);
        Assinatura assinatura = Assinatura.associar(10L, plano, Instant.now());
        Instant momentoTroca = Instant.now();

        assinatura.substituir(momentoTroca);

        assertThat(assinatura.estado()).isEqualTo(EstadoAssinatura.SUBSTITUIDA);
        assertThat(assinatura.dataFim()).isEqualTo(momentoTroca);
    }
}
