package com.tabloide.api.modules.imagem.interfaces.http.dto;

import com.tabloide.api.modules.imagem.domain.FormatoImagem;
import com.tabloide.api.modules.imagem.domain.TipoVinculoImagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrarImagemRequest(
        @NotBlank String nomeBusca,
        @NotNull TipoVinculoImagem tipoVinculo,
        Long vinculoId,
        @NotBlank String urlOuChave,
        @NotNull FormatoImagem formato,
        @Positive long tamanhoBytes
) {
}
