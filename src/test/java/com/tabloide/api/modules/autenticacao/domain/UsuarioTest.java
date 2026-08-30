package com.tabloide.api.modules.autenticacao.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    private static Usuario usuarioAtivo() {
        Instant agora = Instant.now();
        return new Usuario(1L, "dono@sgtm.local", "hash", Perfil.DONO, 10L, null, true, 0, null, agora, agora);
    }

    @Test
    void deveBloquearTemporariamenteAposAtingirMaximoDeTentativas() {
        Usuario usuario = usuarioAtivo();
        Instant agora = Instant.now();

        for (int i = 0; i < 5; i++) {
            usuario.registrarTentativaInvalida(agora, 5, Duration.ofMinutes(15));
        }

        assertThat(usuario.estaBloqueadoTemporariamente(agora.plusSeconds(1))).isTrue();
        assertThat(usuario.estaBloqueadoTemporariamente(agora.plus(Duration.ofMinutes(16)))).isFalse();
    }

    @Test
    void naoDeveBloquearAntesDoLimiteDeTentativas() {
        Usuario usuario = usuarioAtivo();
        Instant agora = Instant.now();

        usuario.registrarTentativaInvalida(agora, 5, Duration.ofMinutes(15));
        usuario.registrarTentativaInvalida(agora, 5, Duration.ofMinutes(15));

        assertThat(usuario.estaBloqueadoTemporariamente(agora)).isFalse();
    }

    @Test
    void loginBemSucedidoDeveZerarTentativasEBloqueio() {
        Usuario usuario = usuarioAtivo();
        Instant agora = Instant.now();
        for (int i = 0; i < 5; i++) {
            usuario.registrarTentativaInvalida(agora, 5, Duration.ofMinutes(15));
        }

        usuario.registrarLoginBemSucedido(agora);

        assertThat(usuario.tentativasLoginInvalidas()).isZero();
        assertThat(usuario.bloqueadoAte()).isNull();
    }

    @Test
    void superAdminNaoPodeSolicitarRedefinicaoDeSenha() {
        Instant agora = Instant.now();
        Usuario superAdmin = new Usuario(1L, "admin@sgtm.local", "hash", Perfil.SUPER_ADMIN, null, null, true, 0, null, agora, agora);

        assertThat(superAdmin.podeSolicitarRedefinicaoSenha()).isFalse();
    }

    @Test
    void usuarioInativoNaoPodeSolicitarRedefinicaoDeSenha() {
        Instant agora = Instant.now();
        Usuario inativo = new Usuario(1L, "dono@sgtm.local", "hash", Perfil.DONO, 10L, null, false, 0, null, agora, agora);

        assertThat(inativo.podeSolicitarRedefinicaoSenha()).isFalse();
    }

    @Test
    void donoAtivoPodeSolicitarRedefinicaoDeSenha() {
        assertThat(usuarioAtivo().podeSolicitarRedefinicaoSenha()).isTrue();
    }

    @Test
    void devePertencerAoSupermercadoComMesmoCnpj() {
        Cnpj cnpj = new Cnpj("11222333000181");
        Instant agora = Instant.now();
        Usuario usuario = new Usuario(1L, "dono@sgtm.local", "hash", Perfil.DONO, 10L, cnpj, true, 0, null, agora, agora);

        assertThat(usuario.pertenceAoSupermercado(cnpj)).isTrue();
        assertThat(usuario.pertenceAoSupermercado(new Cnpj("11444777000161"))).isFalse();
    }
}
