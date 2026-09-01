package com.tabloide.api.modules.qrcodes.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.qrcodes.domain.exceptions.TransicaoEstadoQrCodeInvalidaException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class QrCodeTest {

    private static QrCode gerarQrCode() {
        return QrCode.gerar(1L, 2L, "  QR Entrada  ", Instant.now());
    }

    @Test
    void deveIniciarComEstadoAtivo() {
        assertThat(gerarQrCode().estaAtivo()).isTrue();
    }

    @Test
    void deveGerarCodigoPublicoNaoVazio() {
        QrCode qrCode = gerarQrCode();
        assertThat(qrCode.codigoPublico()).isNotBlank();
    }

    @Test
    void deveGerarCodigosPublicosDiferentesParaCadaQrCode() {
        assertThat(gerarQrCode().codigoPublico()).isNotEqualTo(gerarQrCode().codigoPublico());
    }

    @Test
    void deveNormalizarNomeRemovendoEspacosEDiferencaDeCaixa() {
        QrCode qrCode = gerarQrCode();
        assertThat(qrCode.nome()).isEqualTo("QR Entrada");
        assertThat(qrCode.nomeNormalizado()).isEqualTo("qr entrada");
        assertThat(QrCode.normalizarNome("  QR Entrada ")).isEqualTo(qrCode.nomeNormalizado());
    }

    @Test
    void deveDesativarQuandoAtivo() {
        QrCode qrCode = gerarQrCode();

        qrCode.desativar(Instant.now());

        assertThat(qrCode.estaAtivo()).isFalse();
    }

    @Test
    void naoDeveDesativarQuandoJaDesativado() {
        QrCode qrCode = new QrCode(1L, 1L, 2L, "QR", "qr", "abc123", EstadoQrCode.DESATIVADO, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> qrCode.desativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoQrCodeInvalidaException.class);
    }

    @Test
    void deveAtivarQuandoDesativado() {
        QrCode qrCode = new QrCode(1L, 1L, 2L, "QR", "qr", "abc123", EstadoQrCode.DESATIVADO, 0L, Instant.now(), Instant.now());

        qrCode.ativar(Instant.now());

        assertThat(qrCode.estaAtivo()).isTrue();
    }

    @Test
    void naoDeveAtivarQuandoJaAtivo() {
        QrCode qrCode = gerarQrCode();

        assertThatThrownBy(() -> qrCode.ativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoQrCodeInvalidaException.class);
    }

    @Test
    void deveCompararVersaoConhecida() {
        QrCode qrCode = new QrCode(1L, 1L, 2L, "QR", "qr", "abc123", EstadoQrCode.ATIVO, 3L, Instant.now(), Instant.now());

        assertThat(qrCode.possuiVersao(3L)).isTrue();
        assertThat(qrCode.possuiVersao(2L)).isFalse();
    }
}
