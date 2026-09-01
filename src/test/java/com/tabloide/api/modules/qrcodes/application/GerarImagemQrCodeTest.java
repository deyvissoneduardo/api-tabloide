package com.tabloide.api.modules.qrcodes.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.tabloide.api.modules.qrcodes.domain.QrCode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class GerarImagemQrCodeTest {

    private final GerarImagemQrCode gerarImagemQrCode = new GerarImagemQrCode("https://tabloide.sgtm.example");

    private static QrCode qrCode() {
        return QrCode.gerar(1L, 2L, "QR Entrada", Instant.now());
    }

    @Test
    void devePngComecarComAssinaturaPng() {
        byte[] png = gerarImagemQrCode.paraPng(qrCode());

        assertThat(png).isNotEmpty();
        assertThat(png[0]).isEqualTo((byte) 0x89);
        assertThat(png[1]).isEqualTo((byte) 'P');
        assertThat(png[2]).isEqualTo((byte) 'N');
        assertThat(png[3]).isEqualTo((byte) 'G');
    }

    @Test
    void deveSvgConterMarcacaoSvg() {
        byte[] svg = gerarImagemQrCode.paraSvg(qrCode());

        String conteudo = new String(svg, StandardCharsets.UTF_8);
        assertThat(conteudo).startsWith("<svg");
        assertThat(conteudo).endsWith("</svg>");
    }
}
