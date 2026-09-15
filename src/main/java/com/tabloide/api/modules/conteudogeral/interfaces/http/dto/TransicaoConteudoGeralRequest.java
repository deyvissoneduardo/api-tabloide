package com.tabloide.api.modules.conteudogeral.interfaces.http.dto;

import jakarta.validation.constraints.NotNull;

public record TransicaoConteudoGeralRequest(@NotNull Long versao) {
}
