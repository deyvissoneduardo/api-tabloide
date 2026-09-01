package com.tabloide.api.modules.imagem.interfaces.http.dto;

import com.tabloide.api.modules.imagem.domain.FormatoImagem;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.TipoVinculoImagem;
import java.time.Instant;

public record ImagemResponse(
        Long id,
        Long supermercadoId,
        String nomeBusca,
        TipoVinculoImagem tipoVinculo,
        Long vinculoId,
        String urlOuChave,
        FormatoImagem formato,
        long tamanhoBytes,
        Instant uploadEm,
        Instant excluidoEm
) {

    public static ImagemResponse from(Imagem imagem) {
        return new ImagemResponse(
                imagem.id(),
                imagem.supermercadoId(),
                imagem.nomeBusca(),
                imagem.tipoVinculo(),
                imagem.vinculoId(),
                imagem.urlOuChave(),
                imagem.formato(),
                imagem.tamanhoBytes(),
                imagem.uploadEm(),
                imagem.excluidoEm()
        );
    }
}
