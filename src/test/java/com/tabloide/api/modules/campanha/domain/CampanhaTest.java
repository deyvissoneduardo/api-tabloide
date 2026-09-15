package com.tabloide.api.modules.campanha.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.campanha.domain.exceptions.LojasCampanhaInvalidasException;
import com.tabloide.api.modules.campanha.domain.exceptions.PeriodoCampanhaInvalidoException;
import com.tabloide.api.modules.campanha.domain.exceptions.TransicaoEstadoCampanhaInvalidaException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CampanhaTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Test
    void naoDeveCadastrarSemLojas() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(), Set.of(), agora, agora.plus(1, ChronoUnit.DAYS), false, agora))
                .isInstanceOf(LojasCampanhaInvalidasException.class);
    }

    @Test
    void naoDeveCadastrarComInicioPosteriorAoFim() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), Set.of(), agora, agora.minus(1, ChronoUnit.DAYS), false, agora))
                .isInstanceOf(PeriodoCampanhaInvalidoException.class);
    }

    @Test
    void deveIniciarComoRascunhoQuandoNaoConfirmarPublicacao() {
        Instant agora = Instant.now();
        Campanha campanha = Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), Set.of(), agora, agora.plus(1, ChronoUnit.DAYS), false, agora);

        assertThat(campanha.estado()).isEqualTo(EstadoCampanha.RASCUNHO);
    }

    @Test
    void deveFicarVigenteQuandoPublicadaDentroDoPeriodo() {
        Instant agora = Instant.now();
        Campanha campanha = Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), Set.of(), agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), true, agora);

        assertThat(campanha.estado()).isEqualTo(EstadoCampanha.VIGENTE);
    }

    @Test
    void deveFicarAgendadaQuandoPublicadaComInicioFuturo() {
        Instant agora = Instant.now();
        Campanha campanha = Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), Set.of(), agora.plus(1, ChronoUnit.DAYS), agora.plus(2, ChronoUnit.DAYS), true, agora);

        assertThat(campanha.estado()).isEqualTo(EstadoCampanha.AGENDADA);
    }

    @Test
    void deveTratarOfertasNulasComoConjuntoVazio() {
        Instant agora = Instant.now();
        Campanha campanha = Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), null, agora, agora.plus(1, ChronoUnit.DAYS), false, agora);

        assertThat(campanha.ofertaIds()).isEmpty();
        assertThat(campanha.quantidadeOfertas()).isZero();
    }

    @Test
    void deveCancelarQuandoNaoFinalizada() {
        Instant agora = Instant.now();
        Campanha campanha = Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), Set.of(), agora, agora.plus(1, ChronoUnit.DAYS), false, agora);

        campanha.cancelar(agora);

        assertThat(campanha.estado()).isEqualTo(EstadoCampanha.CANCELADA);
        assertThat(campanha.estaFinalizada()).isTrue();
    }

    @Test
    void naoDeveCancelarQuandoJaFinalizada() {
        Instant agora = Instant.now();
        Campanha campanha = Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), Set.of(), agora, agora.plus(1, ChronoUnit.DAYS), false, agora);
        campanha.cancelar(agora);

        assertThatThrownBy(() -> campanha.cancelar(agora))
                .isInstanceOf(TransicaoEstadoCampanhaInvalidaException.class);
    }

    @Test
    void naoDeveEditarQuandoFinalizada() {
        Instant agora = Instant.now();
        Campanha campanha = Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), Set.of(), agora, agora.plus(1, ChronoUnit.DAYS), false, agora);
        campanha.cancelar(agora);

        assertThatThrownBy(() -> campanha.editar("Nova", null, Set.of(100L), Set.of(), agora, agora.plus(2, ChronoUnit.DAYS), agora))
                .isInstanceOf(TransicaoEstadoCampanhaInvalidaException.class);
    }

    @Test
    void deveEditarQuandoNaoFinalizada() {
        Instant agora = Instant.now();
        Campanha campanha = Campanha.cadastrar(
                SUPERMERCADO_ID, "Campanha", null, Set.of(100L), Set.of(), agora, agora.plus(1, ChronoUnit.DAYS), false, agora);

        campanha.editar("Campanha Editada", "Descrição", Set.of(200L), Set.of(300L), agora, agora.plus(3, ChronoUnit.DAYS), agora);

        assertThat(campanha.nome()).isEqualTo("Campanha Editada");
        assertThat(campanha.lojaIds()).containsExactly(200L);
        assertThat(campanha.ofertaIds()).containsExactly(300L);
    }
}
