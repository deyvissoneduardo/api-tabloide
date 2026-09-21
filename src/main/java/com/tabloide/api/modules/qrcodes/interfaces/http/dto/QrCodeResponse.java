package com.tabloide.api.modules.qrcodes.interfaces.http.dto;

import com.tabloide.api.modules.qrcodes.domain.EstadoQrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import java.time.Instant;

public record QrCodeResponse(
        Long id,
        Long supermercadoId,
        Long lojaId,
        String nome,
        String codigoPublico,
        EstadoQrCode estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm,
        // US-107: quantidade de acessos (scans) que este QR Code originou no período consultado.
        // Nulo quando o período não é aplicável ao contexto da resposta (ex.: geração/ativação/desativação).
        Long quantidadeAcessosNoPeriodo
) {

    public static QrCodeResponse from(QrCode qrCode) {
        return from(qrCode, null);
    }

    public static QrCodeResponse from(QrCode qrCode, Long quantidadeAcessosNoPeriodo) {
        return new QrCodeResponse(
                qrCode.id(),
                qrCode.supermercadoId(),
                qrCode.lojaId(),
                qrCode.nome(),
                qrCode.codigoPublico(),
                qrCode.estado(),
                qrCode.versao(),
                qrCode.criadoEm(),
                qrCode.atualizadoEm(),
                quantidadeAcessosNoPeriodo
        );
    }
}
