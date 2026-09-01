package com.tabloide.api.modules.autenticacao.interfaces.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AlterarPropriosDadosRequest(
        @NotBlank
        String senhaAtual,

        @Email
        String novoEmail,

        @Pattern(
                regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$",
                message = "Senha deve ter ao menos 6 caracteres, com uma maiúscula, uma minúscula e um número"
        )
        String novaSenha
) {
}
