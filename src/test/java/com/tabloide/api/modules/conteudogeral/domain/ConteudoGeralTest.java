package com.tabloide.api.modules.conteudogeral.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.conteudogeral.domain.exceptions.TransicaoEstadoConteudoGeralInvalidaException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ConteudoGeralTest {

    @Test
    void deveIniciarComoRascunho() {
        ConteudoGeral conteudoGeral = ConteudoGeral.criar(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", Instant.now());

        assertThat(conteudoGeral.estado()).isEqualTo(EstadoConteudoGeral.RASCUNHO);
    }

    @Test
    void deveEditarQuandoEmRascunho() {
        ConteudoGeral conteudoGeral = ConteudoGeral.criar(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", Instant.now());

        conteudoGeral.editar("Termos v2", "Corpo v2", Instant.now());

        assertThat(conteudoGeral.titulo()).isEqualTo("Termos v2");
        assertThat(conteudoGeral.corpo()).isEqualTo("Corpo v2");
    }

    @Test
    void naoDeveEditarQuandoPublicado() {
        ConteudoGeral conteudoGeral = ConteudoGeral.criar(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", Instant.now());
        conteudoGeral.publicar(Instant.now());

        assertThatThrownBy(() -> conteudoGeral.editar("Novo", "Novo", Instant.now()))
                .isInstanceOf(TransicaoEstadoConteudoGeralInvalidaException.class);
    }

    @Test
    void devePublicarQuandoEmRascunho() {
        ConteudoGeral conteudoGeral = ConteudoGeral.criar(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", Instant.now());

        conteudoGeral.publicar(Instant.now());

        assertThat(conteudoGeral.estado()).isEqualTo(EstadoConteudoGeral.PUBLICADO);
        assertThat(conteudoGeral.publicadoEm()).isNotNull();
    }

    @Test
    void naoDevePublicarQuandoJaPublicado() {
        ConteudoGeral conteudoGeral = ConteudoGeral.criar(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", Instant.now());
        conteudoGeral.publicar(Instant.now());

        assertThatThrownBy(() -> conteudoGeral.publicar(Instant.now()))
                .isInstanceOf(TransicaoEstadoConteudoGeralInvalidaException.class);
    }

    @Test
    void deveArquivarQuandoPublicado() {
        ConteudoGeral conteudoGeral = ConteudoGeral.criar(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", Instant.now());
        conteudoGeral.publicar(Instant.now());

        conteudoGeral.arquivar(Instant.now());

        assertThat(conteudoGeral.estado()).isEqualTo(EstadoConteudoGeral.ARQUIVADO);
    }

    @Test
    void naoDeveArquivarQuandoEmRascunho() {
        ConteudoGeral conteudoGeral = ConteudoGeral.criar(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", Instant.now());

        assertThatThrownBy(() -> conteudoGeral.arquivar(Instant.now()))
                .isInstanceOf(TransicaoEstadoConteudoGeralInvalidaException.class);
    }
}
