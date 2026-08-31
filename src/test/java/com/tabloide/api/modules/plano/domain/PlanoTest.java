package com.tabloide.api.modules.plano.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PlanoTest {

    @Test
    void deveNormalizarNomeAoCriar() {
        Instant agora = Instant.now();

        Plano plano = Plano.criar("  Plano Ouro  ", 30, BigDecimal.TEN, 100, null, agora);

        assertThat(plano.nome()).isEqualTo("Plano Ouro");
        assertThat(plano.nomeNormalizado()).isEqualTo("plano ouro");
        assertThat(plano.estaExcluido()).isFalse();
        assertThat(plano.versao()).isNull();
    }

    @Test
    void deveAtualizarCamposENomeNormalizadoAoEditar() {
        Instant agora = Instant.now();
        Plano plano = Plano.criar("Básico", 7, BigDecimal.ZERO, 100, null, agora);

        Instant depois = agora.plusSeconds(60);
        plano.editar("  Premium  ", 60, BigDecimal.valueOf(199.90), 500, 10, depois);

        assertThat(plano.nome()).isEqualTo("Premium");
        assertThat(plano.nomeNormalizado()).isEqualTo("premium");
        assertThat(plano.validadeDias()).isEqualTo(60);
        assertThat(plano.valor()).isEqualByComparingTo(BigDecimal.valueOf(199.90));
        assertThat(plano.limiteFotos()).isEqualTo(500);
        assertThat(plano.limiteLojas()).isEqualTo(10);
        assertThat(plano.atualizadoEm()).isEqualTo(depois);
    }

    @Test
    void excluirLogicamenteDeveSerIdempotente() {
        Instant agora = Instant.now();
        Plano plano = Plano.criar("Básico", 7, BigDecimal.ZERO, 100, null, agora);

        plano.excluirLogicamente(agora);
        Instant primeiraExclusao = plano.excluidoEm();

        plano.excluirLogicamente(agora.plusSeconds(120));

        assertThat(plano.estaExcluido()).isTrue();
        assertThat(plano.excluidoEm()).isEqualTo(primeiraExclusao);
    }

    @Test
    void possuiVersaoDeveCompararComNullQuandoPlanoNovo() {
        Plano plano = Plano.criar("Básico", 7, BigDecimal.ZERO, 100, null, Instant.now());

        assertThat(plano.possuiVersao(null)).isTrue();
        assertThat(plano.possuiVersao(1L)).isFalse();
    }
}
