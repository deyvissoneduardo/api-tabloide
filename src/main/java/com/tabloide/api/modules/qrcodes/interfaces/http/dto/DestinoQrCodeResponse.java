package com.tabloide.api.modules.qrcodes.interfaces.http.dto;

import com.tabloide.api.modules.qrcodes.application.DestinoQrCode;

public record DestinoQrCodeResponse(Long supermercadoId, Long lojaId) {

    public static DestinoQrCodeResponse from(DestinoQrCode destino) {
        return new DestinoQrCodeResponse(destino.supermercadoId(), destino.lojaId());
    }
}
