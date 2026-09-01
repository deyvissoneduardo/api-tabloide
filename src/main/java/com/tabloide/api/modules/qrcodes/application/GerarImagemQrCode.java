package com.tabloide.api.modules.qrcodes.application;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GerarImagemQrCode {

    private static final int TAMANHO_PADRAO_PX = 300;

    private final String urlBasePublica;

    public GerarImagemQrCode(@Value("${app.public-base-url}") String urlBasePublica) {
        this.urlBasePublica = urlBasePublica;
    }

    public byte[] paraPng(QrCode qrCode) {
        BitMatrix matriz = gerarMatriz(qrCode);
        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matriz, "PNG", saida);
            return saida.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public byte[] paraSvg(QrCode qrCode) {
        BitMatrix matriz = gerarMatriz(qrCode);
        int largura = matriz.getWidth();
        int altura = matriz.getHeight();

        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 ")
                .append(largura).append(' ').append(altura)
                .append("\" shape-rendering=\"crispEdges\">");
        svg.append("<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>");
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                if (matriz.get(x, y)) {
                    svg.append("<rect x=\"").append(x).append("\" y=\"").append(y)
                            .append("\" width=\"1\" height=\"1\" fill=\"#000000\"/>");
                }
            }
        }
        svg.append("</svg>");

        return svg.toString().getBytes(StandardCharsets.UTF_8);
    }

    private BitMatrix gerarMatriz(QrCode qrCode) {
        String destino = urlBasePublica + "/q/" + qrCode.codigoPublico();
        try {
            return new QRCodeWriter().encode(destino, BarcodeFormat.QR_CODE, TAMANHO_PADRAO_PX, TAMANHO_PADRAO_PX);
        } catch (WriterException ex) {
            throw new IllegalStateException("Falha ao gerar QR Code", ex);
        }
    }
}
