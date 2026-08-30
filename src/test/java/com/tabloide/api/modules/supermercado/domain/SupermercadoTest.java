package com.tabloide.api.modules.supermercado.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.TransicaoEstadoInvalidaException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SupermercadoTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    private static Supermercado cadastrarSupermercado() {
        return Supermercado.cadastrar(
                new Cnpj("11222333000181"),
                "Razão Social LTDA",
                "Mercado Bom Preço",
                "contato@mercado.com",
                "11999998888",
                ENDERECO,
                null,
                null,
                null,
                Instant.now()
        );
    }

    @Test
    void deveIniciarComEstadoAtivo() {
        assertThat(cadastrarSupermercado().estaAtivo()).isTrue();
    }

    @Test
    void deveEditarDadosQuandoAtivo() {
        Supermercado supermercado = cadastrarSupermercado();

        supermercado.editarDados("Nova Razão", "Novo Fantasia", "novo@mercado.com", "11888887777", ENDERECO, "Sala 2", null, null, Instant.now());

        assertThat(supermercado.razaoSocial()).isEqualTo("Nova Razão");
        assertThat(supermercado.nomeFantasia()).isEqualTo("Novo Fantasia");
    }

    @Test
    void naoDeveEditarQuandoBloqueado() {
        Supermercado supermercado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> supermercado.editarDados("X", "Y", "e@e.com", "119999", ENDERECO, null, null, null, Instant.now()))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void naoDeveEditarQuandoDesativado() {
        Supermercado supermercado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.DESATIVADO, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> supermercado.editarDados("X", "Y", "e@e.com", "119999", ENDERECO, null, null, null, Instant.now()))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void deveAtivarQuandoBloqueadoOuDesativado() {
        Supermercado bloqueado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());
        bloqueado.ativar(Instant.now());
        assertThat(bloqueado.estaAtivo()).isTrue();

        Supermercado desativado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.DESATIVADO, 0L, Instant.now(), Instant.now());
        desativado.ativar(Instant.now());
        assertThat(desativado.estaAtivo()).isTrue();
    }

    @Test
    void naoDeveAtivarQuandoJaAtivo() {
        Supermercado supermercado = cadastrarSupermercado();

        assertThatThrownBy(() -> supermercado.ativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }

    @Test
    void deveDesativarQuandoAtivoOuBloqueado() {
        Supermercado ativo = cadastrarSupermercado();
        ativo.desativar(Instant.now());
        assertThat(ativo.estaDesativado()).isTrue();

        Supermercado bloqueado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());
        bloqueado.desativar(Instant.now());
        assertThat(bloqueado.estaDesativado()).isTrue();
    }

    @Test
    void naoDeveDesativarQuandoJaDesativado() {
        Supermercado supermercado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.DESATIVADO, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> supermercado.desativar(Instant.now()))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }

    @Test
    void deveBloquearQuandoAtivo() {
        Supermercado supermercado = cadastrarSupermercado();

        supermercado.bloquear(Instant.now());

        assertThat(supermercado.estaBloqueado()).isTrue();
    }

    @Test
    void naoDeveBloquearQuandoJaBloqueado() {
        Supermercado supermercado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> supermercado.bloquear(Instant.now()))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }

    @Test
    void naoDeveBloquearQuandoDesativado() {
        Supermercado supermercado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.DESATIVADO, 0L, Instant.now(), Instant.now());

        assertThatThrownBy(() -> supermercado.bloquear(Instant.now()))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }

    @Test
    void deveComparaVersaoConhecida() {
        Supermercado supermercado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, EstadoSupermercado.ATIVO, 3L, Instant.now(), Instant.now());

        assertThat(supermercado.possuiVersao(3L)).isTrue();
        assertThat(supermercado.possuiVersao(2L)).isFalse();
    }
}
