package com.tabloide.api.modules.conteudopromocional.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.conteudopromocional.domain.exceptions.DestinoBannerInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.LojasConteudoPromocionalInvalidasException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.NivelAvisoInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.PeriodoConteudoPromocionalInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.TextoConteudoPromocionalInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.TituloConteudoPromocionalInvalidoException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConteudoPromocionalTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long LOJA_ID = 100L;

    @Test
    void naoDeveCadastrarSemTitulo() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, " ", "Texto", null, null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora))
                .isInstanceOf(TituloConteudoPromocionalInvalidoException.class);
    }

    @Test
    void naoDeveCadastrarSemLojas() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, "Título", "Texto", null, null,
                Set.of(), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora))
                .isInstanceOf(LojasConteudoPromocionalInvalidasException.class);
    }

    @Test
    void naoDeveCadastrarComPeriodoInvalido() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, "Título", "Texto", null, null,
                Set.of(LOJA_ID), agora, agora.minus(1, ChronoUnit.DAYS), 0, agora))
                .isInstanceOf(PeriodoConteudoPromocionalInvalidoException.class);
    }

    @Test
    void mensagemExigeTexto() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, "Título", null, null, null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora))
                .isInstanceOf(TextoConteudoPromocionalInvalidoException.class);
    }

    @Test
    void mensagemNaoAceitaNivel() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, "Título", "Texto", NivelAviso.URGENTE, null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora))
                .isInstanceOf(NivelAvisoInvalidoException.class);
    }

    @Test
    void avisoExigeNivel() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.AVISO, "Título", "Texto", null, null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora))
                .isInstanceOf(NivelAvisoInvalidoException.class);
    }

    @Test
    void deveCadastrarAvisoComTextoENivel() {
        Instant agora = Instant.now();
        ConteudoPromocional aviso = ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.AVISO, "Título", "Texto", NivelAviso.ATENCAO, null,
                Set.of(LOJA_ID), agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), 0, agora);

        assertThat(aviso.nivel()).isEqualTo(NivelAviso.ATENCAO);
        assertThat(aviso.estado()).isEqualTo(EstadoConteudoPromocional.VIGENTE);
    }

    @Test
    void bannerNaoAceitaTexto() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.BANNER, "Título", "Texto", null, null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora))
                .isInstanceOf(TextoConteudoPromocionalInvalidoException.class);
    }

    @Test
    void bannerAceitaDestinoInternoOuHttps() {
        Instant agora = Instant.now();
        ConteudoPromocional banner = ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.BANNER, "Título", null, null, "https://exemplo.com/promo",
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora);

        assertThat(banner.destino()).isEqualTo("https://exemplo.com/promo");
    }

    @Test
    void bannerRejeitaDestinoComFormatoInvalido() {
        Instant agora = Instant.now();
        assertThatThrownBy(() -> ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.BANNER, "Título", null, null, "http://inseguro.com",
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora))
                .isInstanceOf(DestinoBannerInvalidoException.class);
    }

    @Test
    void bannerPermiteDestinoAusente() {
        Instant agora = Instant.now();
        ConteudoPromocional banner = ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.BANNER, "Título", null, null, null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora);

        assertThat(banner.destino()).isNull();
    }

    @Test
    void deveReordenarAtribuindoNovaPosicao() {
        Instant agora = Instant.now();
        ConteudoPromocional conteudo = ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, "Título", "Texto", null, null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, agora);

        conteudo.atribuirPosicao(3, agora);

        assertThat(conteudo.posicao()).isEqualTo(3);
    }

    @Test
    void estadoEfetivoDeveApresentarExpiradoQuandoVigenteVencido() {
        Instant agora = Instant.now();
        ConteudoPromocional conteudo = ConteudoPromocional.cadastrar(
                SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, "Título", "Texto", null, null,
                Set.of(LOJA_ID), agora.minus(2, ChronoUnit.DAYS), agora.minus(1, ChronoUnit.DAYS), 0, agora.minus(2, ChronoUnit.DAYS));

        assertThat(conteudo.estado()).isEqualTo(EstadoConteudoPromocional.VIGENTE);
        assertThat(conteudo.estadoEfetivo(agora)).isEqualTo(EstadoConteudoPromocional.EXPIRADO);
    }
}
