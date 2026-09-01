package com.tabloide.api.modules.imagem.application;

import com.tabloide.api.modules.imagem.domain.FormatoImagem;
import com.tabloide.api.modules.imagem.domain.TipoVinculoImagem;

public record DadosImagem(
        String nomeBusca,
        TipoVinculoImagem tipoVinculo,
        Long vinculoId,
        String urlOuChave,
        FormatoImagem formato,
        long tamanhoBytes
) {
}
