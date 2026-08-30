package com.tabloide.api.modules.autenticacao.interfaces.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerificarRedefinicaoSenhaRequest(
        @NotBlank @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos, sem pontuação") String cnpj,
        @NotBlank @Email String email
) {
}
