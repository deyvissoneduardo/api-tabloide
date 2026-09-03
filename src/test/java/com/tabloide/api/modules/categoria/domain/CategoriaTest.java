package com.tabloide.api.modules.categoria.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.categoria.domain.exceptions.TransicaoEstadoInvalidaException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CategoriaTest {

    private static Categoria cadastrarCategoria() {
        return Categoria.cadastrar(1L, "  Bebidas  ", "  Cervejas e refrigerantes  ", Instant.now());
    }

    @Test
    void deveIniciarComEstadoAtiva() {
        assertThat(cadastrarCategoria().estaAtiva()).isTrue();
    }

    @Test
    void deveNormalizarNomeEDescricaoRemovendoEspacosExternos() {
        Categoria categoria = cadastrarCategoria();

        assertThat(categoria.nome()).isEqualTo("Bebidas");
        assertThat(categoria.descricao()).isEqualTo("Cervejas e refrigerantes");
    }

    @Test
    void deveAceitarDescricaoAusente() {
        Categoria categoria = Categoria.cadastrar(1L, "Bebidas", null, Instant.now());

        assertThat(categoria.descricao()).isNull();
    }

    @Test
    void deveEditarNomeEDescricao() {
        Categoria categoria = cadastrarCategoria();

        categoria.editar("Bebidas Geladas", null, Instant.now());

        assertThat(categoria.nome()).isEqualTo("Bebidas Geladas");
        assertThat(categoria.descricao()).isNull();
    }

    @Test
    void deveDesativarQuandoAtiva() {
        Categoria categoria = cadastrarCategoria();

        categoria.desativar(Instant.now());

        assertThat(categoria.estaAtiva()).isFalse();
    }

    @Test
    void naoDeveDesativarQuandoJaDesativada() {
        Categoria categoria = new Categoria(1L, 1L, "Bebidas", null, EstadoCategoria.DESATIVADA, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> categoria.desativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }

    @Test
    void deveAtivarQuandoDesativada() {
        Categoria categoria = new Categoria(1L, 1L, "Bebidas", null, EstadoCategoria.DESATIVADA, 0L, Instant.now(), Instant.now());

        categoria.ativar(Instant.now());

        assertThat(categoria.estaAtiva()).isTrue();
    }

    @Test
    void naoDeveAtivarQuandoJaAtiva() {
        Categoria categoria = cadastrarCategoria();

        assertThatThrownBy(() -> categoria.ativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }

    @Test
    void deveCompararVersaoConhecida() {
        Categoria categoria = new Categoria(1L, 1L, "Bebidas", null, EstadoCategoria.ATIVA, 3L, Instant.now(), Instant.now());

        assertThat(categoria.possuiVersao(3L)).isTrue();
        assertThat(categoria.possuiVersao(2L)).isFalse();
    }
}
