package com.tabloide.api.modules.produto.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.produto.domain.exceptions.TransicaoEstadoProdutoInvalidaException;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProdutoTest {

    @Test
    void naoDeveCadastrarSemCategorias() {
        assertThatThrownBy(() -> Produto.cadastrar(1L, "Produto", Set.of(), null, null, null, null, null, Instant.now()))
                .isInstanceOf(com.tabloide.api.modules.produto.domain.exceptions.CategoriasProdutoInvalidasException.class);
    }

    private static Produto cadastrarProduto() {
        return Produto.cadastrar(1L, "  Refrigerante Cola 2L  ", Set.of(10L, 20L), "  Marca X  ", null, null, null, null, Instant.now());
    }

    @Test
    void deveIniciarComEstadoAtivo() {
        assertThat(cadastrarProduto().estaAtivo()).isTrue();
    }

    @Test
    void deveNormalizarNomeEMarcaRemovendoEspacosExternos() {
        Produto produto = cadastrarProduto();

        assertThat(produto.nome()).isEqualTo("Refrigerante Cola 2L");
        assertThat(produto.marca()).isEqualTo("Marca X");
    }

    @Test
    void deveManterCategoriasAssociadas() {
        Produto produto = cadastrarProduto();

        assertThat(produto.categoriaIds()).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void deveTratarCamposOpcionaisEmBrancoComoAusentes() {
        Produto produto = Produto.cadastrar(1L, "Produto", Set.of(1L), "  ", "", " ", null, "", Instant.now());

        assertThat(produto.marca()).isNull();
        assertThat(produto.descricao()).isNull();
        assertThat(produto.peso()).isNull();
        assertThat(produto.unidade()).isNull();
        assertThat(produto.volume()).isNull();
    }

    @Test
    void deveEditarCamposEditaveis() {
        Produto produto = cadastrarProduto();

        produto.editar("Novo Nome", "Nova Marca", "Nova Descrição", "1kg", "un", "500ml", Instant.now());

        assertThat(produto.nome()).isEqualTo("Novo Nome");
        assertThat(produto.marca()).isEqualTo("Nova Marca");
        assertThat(produto.descricao()).isEqualTo("Nova Descrição");
        assertThat(produto.peso()).isEqualTo("1kg");
        assertThat(produto.unidade()).isEqualTo("un");
        assertThat(produto.volume()).isEqualTo("500ml");
    }

    @Test
    void deveDesativarQuandoAtivo() {
        Produto produto = cadastrarProduto();

        produto.desativar(Instant.now());

        assertThat(produto.estaAtivo()).isFalse();
    }

    @Test
    void naoDeveDesativarQuandoJaDesativado() {
        Produto produto = new Produto(1L, 1L, "Produto", Set.of(10L), null, null, null, null, null,
                EstadoProduto.DESATIVADO, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> produto.desativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoProdutoInvalidaException.class);
    }

    @Test
    void deveAtivarQuandoDesativado() {
        Produto produto = new Produto(1L, 1L, "Produto", Set.of(10L), null, null, null, null, null,
                EstadoProduto.DESATIVADO, 0L, Instant.now(), Instant.now());

        produto.ativar(Instant.now());

        assertThat(produto.estaAtivo()).isTrue();
    }

    @Test
    void naoDeveAtivarQuandoJaAtivo() {
        Produto produto = cadastrarProduto();

        assertThatThrownBy(() -> produto.ativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoProdutoInvalidaException.class);
    }
}
