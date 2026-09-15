package com.tabloide.api.modules.tabloide.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.tabloide.domain.exceptions.ArquivoTabloideInvalidoException;
import com.tabloide.api.modules.tabloide.domain.exceptions.LojasTabloideInvalidasException;
import com.tabloide.api.modules.tabloide.domain.exceptions.PeriodoTabloideInvalidoException;
import com.tabloide.api.modules.tabloide.domain.exceptions.TituloTabloideInvalidoException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TabloideTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long LOJA_ID = 100L;

    @Test
    void naoDeveDisponibilizarSemTitulo() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Tabloide.disponibilizar(
                SUPERMERCADO_ID, "  ", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf", 1024L,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), agora))
                .isInstanceOf(TituloTabloideInvalidoException.class);
    }

    @Test
    void naoDeveDisponibilizarSemLojas() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf", 1024L,
                Set.of(), agora, agora.plus(1, ChronoUnit.DAYS), agora))
                .isInstanceOf(LojasTabloideInvalidasException.class);
    }

    @Test
    void naoDeveDisponibilizarComInicioPosteriorAoFim() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf", 1024L,
                Set.of(LOJA_ID), agora, agora.minus(1, ChronoUnit.DAYS), agora))
                .isInstanceOf(PeriodoTabloideInvalidoException.class);
    }

    @Test
    void naoDeveDisponibilizarPdfSemUrl() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.PDF, null, 1024L,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), agora))
                .isInstanceOf(ArquivoTabloideInvalidoException.class);
    }

    @Test
    void naoDeveDisponibilizarPdfAcimaDe20Mb() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf",
                Tabloide.TAMANHO_MAXIMO_PDF_BYTES + 1, Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), agora))
                .isInstanceOf(ArquivoTabloideInvalidoException.class);
    }

    @Test
    void naoDeveDisponibilizarImagensComUrlDePdf() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.IMAGENS, "https://arquivo/tabloide.pdf", null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), agora))
                .isInstanceOf(ArquivoTabloideInvalidoException.class);
    }

    @Test
    void deveDisponibilizarComPaginasEmImagemSemArquivoPdf() {
        Instant agora = Instant.now();
        Tabloide tabloide = Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.IMAGENS, null, null,
                Set.of(LOJA_ID), agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), agora);

        assertThat(tabloide.tipoArquivo()).isEqualTo(TipoArquivoTabloide.IMAGENS);
        assertThat(tabloide.arquivoPdfUrl()).isNull();
        assertThat(tabloide.estado()).isEqualTo(EstadoTabloide.VIGENTE);
    }

    @Test
    void deveFicarAgendadoQuandoInicioNoFuturo() {
        Instant agora = Instant.now();
        Tabloide tabloide = Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf", 1024L,
                Set.of(LOJA_ID), agora.plus(1, ChronoUnit.DAYS), agora.plus(2, ChronoUnit.DAYS), agora);

        assertThat(tabloide.estado()).isEqualTo(EstadoTabloide.AGENDADO);
    }

    @Test
    void deveFicarVigenteQuandoDentroDoPeriodo() {
        Instant agora = Instant.now();
        Tabloide tabloide = Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf", 1024L,
                Set.of(LOJA_ID), agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), agora);

        assertThat(tabloide.estado()).isEqualTo(EstadoTabloide.VIGENTE);
    }

    @Test
    void estadoEfetivoDeveApresentarExpiradoQuandoVigenteVencido() {
        Instant agora = Instant.now();
        Tabloide tabloide = Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf", 1024L,
                Set.of(LOJA_ID), agora.minus(2, ChronoUnit.DAYS), agora.minus(1, ChronoUnit.DAYS), agora.minus(2, ChronoUnit.DAYS));

        assertThat(tabloide.estado()).isEqualTo(EstadoTabloide.VIGENTE);
        assertThat(tabloide.estadoEfetivo(agora)).isEqualTo(EstadoTabloide.EXPIRADO);
    }
}
