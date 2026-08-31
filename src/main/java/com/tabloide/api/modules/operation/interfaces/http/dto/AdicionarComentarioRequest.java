package com.tabloide.api.modules.operation.interfaces.http.dto;

import jakarta.validation.constraints.NotBlank;

public record AdicionarComentarioRequest(@NotBlank String comentario) {
}
