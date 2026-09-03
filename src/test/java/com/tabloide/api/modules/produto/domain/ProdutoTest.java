package com.tabloide.api.modules.produto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProdutoTest {

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
}
