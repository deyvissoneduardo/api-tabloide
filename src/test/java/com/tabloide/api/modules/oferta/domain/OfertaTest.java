package com.tabloide.api.modules.oferta.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.oferta.domain.exceptions.LojasOfertaInvalidasException;
import com.tabloide.api.modules.oferta.domain.exceptions.PeriodoOfertaInvalidoException;
import com.tabloide.api.modules.oferta.domain.exceptions.PrecoOfertaInvalidoException;
import com.tabloide.api.modules.oferta.domain.exceptions.TransicaoEstadoOfertaInvalidaException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OfertaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long PRODUTO_ID = 10L;
    private static final BigDecimal PRECO_NORMAL = new BigDecimal("10.00");
    private static final BigDecimal PRECO_PROMOCIONAL = new BigDecimal("7.50");

    @Test
    void naoDeveCadastrarSemLojas() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora))
                .isInstanceOf(LojasOfertaInvalidasException.class);
    }

    @Test
    void naoDeveCadastrarComPrecoPromocionalMaiorOuIgualAoNormal() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_NORMAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora))
                .isInstanceOf(PrecoOfertaInvalidoException.class);
    }

    @Test
    void naoDeveCadastrarComPrecoNegativoOuZero() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), BigDecimal.ZERO, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora))
                .isInstanceOf(PrecoOfertaInvalidoException.class);
    }

    @Test
    void naoDeveCadastrarComInicioPosteriorAoFim() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.minus(1, ChronoUnit.DAYS), null, false, agora))
                .isInstanceOf(PeriodoOfertaInvalidoException.class);
    }

    @Test
    void deveIniciarComoRascunhoQuandoNaoConfirmarPublicacao() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);

        assertThat(oferta.estado()).isEqualTo(EstadoOferta.RASCUNHO);
    }

    @Test
    void deveFicarAgendadaQuandoPublicadaComInicioFuturo() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora.plus(1, ChronoUnit.DAYS), agora.plus(2, ChronoUnit.DAYS), null, true, agora);

        assertThat(oferta.estado()).isEqualTo(EstadoOferta.AGENDADA);
    }

    @Test
    void deveFicarVigenteQuandoPublicadaDentroDoPeriodo() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), null, true, agora);

        assertThat(oferta.estado()).isEqualTo(EstadoOferta.VIGENTE);
    }

    @Test
    void deveCalcularPercentualDeDescontoArredondado() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);

        assertThat(oferta.percentualDesconto()).isEqualTo(25);
    }

    @Test
    void deveNormalizarCondicoesEmBrancoComoAusentes() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), "   ", false, agora);

        assertThat(oferta.condicoes()).isNull();
    }

    @Test
    void deveEditarQuandoNaoFinalizada() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);

        oferta.editar(20L, Set.of(200L), new BigDecimal("20.00"), new BigDecimal("15.00"), agora, agora.plus(2, ChronoUnit.DAYS), "Nova condição", agora);

        assertThat(oferta.produtoId()).isEqualTo(20L);
        assertThat(oferta.lojaIds()).containsExactly(200L);
        assertThat(oferta.condicoes()).isEqualTo("Nova condição");
    }

    @Test
    void naoDeveEditarQuandoFinalizada() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);
        oferta.cancelar(agora);

        assertThatThrownBy(() -> oferta.editar(
                PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL, agora, agora.plus(2, ChronoUnit.DAYS), null, agora))
                .isInstanceOf(TransicaoEstadoOfertaInvalidaException.class);
    }

    @Test
    void deveCancelarQuandoNaoFinalizada() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);

        oferta.cancelar(agora);

        assertThat(oferta.estado()).isEqualTo(EstadoOferta.CANCELADA);
        assertThat(oferta.estaFinalizada()).isTrue();
    }

    @Test
    void naoDeveCancelarQuandoJaFinalizada() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);
        oferta.cancelar(agora);

        assertThatThrownBy(() -> oferta.cancelar(agora)).isInstanceOf(TransicaoEstadoOfertaInvalidaException.class);
    }

    @Test
    void deveDesativarQuandoAgendadaOuVigente() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), null, true, agora);

        oferta.desativar(agora);

        assertThat(oferta.estado()).isEqualTo(EstadoOferta.DESATIVADA);
    }

    @Test
    void naoDeveDesativarQuandoRascunho() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);

        assertThatThrownBy(() -> oferta.desativar(agora)).isInstanceOf(TransicaoEstadoOfertaInvalidaException.class);
    }

    @Test
    void deveReativarRespeitandoVigenciaAoAtivar() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora.minus(2, ChronoUnit.DAYS), agora.minus(1, ChronoUnit.DAYS), null, true, agora.minus(2, ChronoUnit.DAYS));
        oferta.desativar(agora.minus(1, ChronoUnit.DAYS));

        oferta.ativar(agora);

        assertThat(oferta.estado()).isEqualTo(EstadoOferta.EXPIRADA);
    }

    @Test
    void naoDeveAtivarQuandoNaoDesativada() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);

        assertThatThrownBy(() -> oferta.ativar(agora)).isInstanceOf(TransicaoEstadoOfertaInvalidaException.class);
    }

    @Test
    void estadoEfetivoDeveApresentarExpiradaQuandoVigenteVencida() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora.minus(2, ChronoUnit.DAYS), agora.minus(1, ChronoUnit.DAYS), null, true, agora.minus(2, ChronoUnit.DAYS));

        assertThat(oferta.estado()).isEqualTo(EstadoOferta.VIGENTE);
        assertThat(oferta.estadoEfetivo(agora)).isEqualTo(EstadoOferta.EXPIRADA);
    }

    @Test
    void estadoEfetivoNaoDeveAlterarEstadosNaoAtivos() {
        Instant agora = Instant.now();
        Oferta oferta = Oferta.cadastrar(
                SUPERMERCADO_ID, PRODUTO_ID, Set.of(100L), PRECO_NORMAL, PRECO_PROMOCIONAL,
                agora, agora.plus(1, ChronoUnit.DAYS), null, false, agora);

        assertThat(oferta.estadoEfetivo(agora.plus(5, ChronoUnit.DAYS))).isEqualTo(EstadoOferta.RASCUNHO);
    }
}
