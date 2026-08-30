package com.tabloide.api.modules.autenticacao.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CnpjTest {

    @Test
    void deveAceitarCnpjValidoSemFormatacao() {
        Cnpj cnpj = new Cnpj("11222333000181");

        assertThat(cnpj.valor()).isEqualTo("11222333000181");
    }

    @Test
    void deveNormalizarCnpjComPontuacao() {
        Cnpj cnpj = new Cnpj("11.222.333/0001-81");

        assertThat(cnpj.valor()).isEqualTo("11222333000181");
    }

    @Test
    void deveRejeitarCnpjComDigitoVerificadorInvalido() {
        assertThatThrownBy(() -> new Cnpj("11222333000199"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRejeitarCnpjComTodosDigitosIguais() {
        assertThatThrownBy(() -> new Cnpj("11111111111111"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRejeitarCnpjComTamanhoInvalido() {
        assertThatThrownBy(() -> new Cnpj("123"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void doisCnpjsComMesmoValorDevemSerIguais() {
        assertThat(new Cnpj("11222333000181")).isEqualTo(new Cnpj("11.222.333/0001-81"));
    }
}
