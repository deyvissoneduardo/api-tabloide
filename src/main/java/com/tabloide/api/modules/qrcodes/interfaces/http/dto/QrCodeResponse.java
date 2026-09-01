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
        Instant atualizadoEm
) {

    public static QrCodeResponse from(QrCode qrCode) {
        return new QrCodeResponse(
                qrCode.id(),
                qrCode.supermercadoId(),
                qrCode.lojaId(),
                qrCode.nome(),
                qrCode.codigoPublico(),
                qrCode.estado(),
                qrCode.versao(),
                qrCode.criadoEm(),
                qrCode.atualizadoEm()
        );
    }
}
