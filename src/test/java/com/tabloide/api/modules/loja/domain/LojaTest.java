package com.tabloide.api.modules.loja.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.loja.domain.exceptions.TransicaoEstadoInvalidaException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class LojaTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    private static Loja cadastrarLoja() {
        return Loja.cadastrar(1L, "  Loja Centro  ", ENDERECO, null, Instant.now());
    }

    @Test
    void deveIniciarComEstadoAtiva() {
        assertThat(cadastrarLoja().estaAtiva()).isTrue();
    }

    @Test
    void deveNormalizarNomeRemovendoEspacosEDiferencaDeCaixa() {
        Loja loja = cadastrarLoja();
        assertThat(loja.nome()).isEqualTo("Loja Centro");
        assertThat(loja.nomeNormalizado()).isEqualTo("loja centro");
        assertThat(Loja.normalizarNome("  LOJA Centro ")).isEqualTo(loja.nomeNormalizado());
    }

    @Test
    void deveDesativarQuandoAtiva() {
        Loja loja = cadastrarLoja();

        loja.desativar(Instant.now());

        assertThat(loja.estaAtiva()).isFalse();
    }

    @Test
    void naoDeveDesativarQuandoJaDesativada() {
        Loja loja = new Loja(1L, 1L, "Loja", "loja", ENDERECO, null, EstadoLoja.DESATIVADA, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> loja.desativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }

    @Test
    void deveAtivarQuandoDesativada() {
        Loja loja = new Loja(1L, 1L, "Loja", "loja", ENDERECO, null, EstadoLoja.DESATIVADA, 0L, Instant.now(), Instant.now());

        loja.ativar(Instant.now());

        assertThat(loja.estaAtiva()).isTrue();
    }

    @Test
    void naoDeveAtivarQuandoJaAtiva() {
        Loja loja = cadastrarLoja();

        assertThatThrownBy(() -> loja.ativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }

    @Test
    void deveCompararVersaoConhecida() {
        Loja loja = new Loja(1L, 1L, "Loja", "loja", ENDERECO, null, EstadoLoja.ATIVA, 3L, Instant.now(), Instant.now());

        assertThat(loja.possuiVersao(3L)).isTrue();
        assertThat(loja.possuiVersao(2L)).isFalse();
    }
}
