package com.tabloide.api.modules.autenticacao.interfaces.http.dto;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CadastrarUsuarioAdministrativoRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$",
                message = "Senha deve ter ao menos 6 caracteres, com uma maiúscula, uma minúscula e um número"
        )
        String senha,

        @NotNull
        Perfil perfil
) {
}
