package com.tabloide.api.modules.supermercado.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EnderecoTest {

    @Test
    void deveCriarEnderecoValido() {
        Endereco endereco = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

        assertThat(endereco.cep()).isEqualTo("01310-100");
        assertThat(endereco.uf()).isEqualTo("SP");
    }

    @Test
    void deveRejeitarCampoObrigatorioEmBranco() {
        assertThatThrownBy(() -> new Endereco("", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRejeitarCampoObrigatorioNulo() {
        assertThatThrownBy(() -> new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
