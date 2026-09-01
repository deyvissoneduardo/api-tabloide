package com.tabloide.api.modules.qrcodes.interfaces.http.dto;

import jakarta.validation.constraints.NotBlank;

public record CadastrarQrCodeRequest(
        @NotBlank String nome
) {
}
