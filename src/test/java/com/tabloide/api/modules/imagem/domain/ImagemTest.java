package com.tabloide.api.modules.imagem.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.imagem.domain.exceptions.FormatoOuTamanhoInvalidoException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ImagemTest {

    @Test
    void deveRegistrarComoAtivaESemVinculo() {
        Imagem imagem = Imagem.registrar(1L, "Banner Black Friday", TipoVinculoImagem.BANNER, null,
                "s3://bucket/banner.jpg", FormatoImagem.JPG, 1024, Instant.now());

        assertThat(imagem.estaAtiva()).isTrue();
        assertThat(imagem.estaVinculada()).isFalse();
        assertThat(imagem.nomeBusca()).isEqualTo("Banner Black Friday");
    }

    @Test
    void deveRejeitarTamanhoAcimaDoLimite() {
        long acimaDoLimite = Imagem.TAMANHO_MAXIMO_BYTES + 1;

        assertThatThrownBy(() -> Imagem.registrar(1L, "Banner", TipoVinculoImagem.BANNER, null,
                "s3://bucket/banner.jpg", FormatoImagem.JPG, acimaDoLimite, Instant.now()))
                .isInstanceOf(FormatoOuTamanhoInvalidoException.class);
    }

    @Test
    void deveRejeitarTamanhoZeroOuNegativo() {
        assertThatThrownBy(() -> Imagem.registrar(1L, "Banner", TipoVinculoImagem.BANNER, null,
                "s3://bucket/banner.jpg", FormatoImagem.JPG, 0, Instant.now()))
                .isInstanceOf(FormatoOuTamanhoInvalidoException.class);
    }

    @Test
    void deveConsiderarVinculadaQuandoVinculoIdPresente() {
        Imagem imagem = Imagem.registrar(1L, "Produto X", TipoVinculoImagem.PRODUTO, 42L,
                "s3://bucket/produto.jpg", FormatoImagem.PNG, 2048, Instant.now());

        assertThat(imagem.estaVinculada()).isTrue();
    }

    @Test
    void excluirDeveMarcarComoInativa() {
        Imagem imagem = Imagem.registrar(1L, "Logo", TipoVinculoImagem.LOGOMARCA, null,
                "s3://bucket/logo.png", FormatoImagem.PNG, 2048, Instant.now());

        imagem.excluir(Instant.now());

        assertThat(imagem.estaAtiva()).isFalse();
    }
}
