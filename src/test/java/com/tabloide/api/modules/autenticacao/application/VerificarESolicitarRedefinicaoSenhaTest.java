package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenha;
import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenhaRepository;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.DadosRedefinicaoNaoConferemException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerificarESolicitarRedefinicaoSenhaTest {

    private static final String CNPJ_VALIDO = "11222333000181";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RedefinicaoSenhaRepository redefinicaoSenhaRepository;

    private VerificarESolicitarRedefinicaoSenha verificarESolicitarRedefinicaoSenha;

    @BeforeEach
    void configurar() {
        AutenticacaoProperties propriedades = new AutenticacaoProperties(
                new AutenticacaoProperties.Sessao(Duration.ofHours(8), Duration.ofMinutes(30)),
                new AutenticacaoProperties.Login(5, Duration.ofMinutes(15)),
                new AutenticacaoProperties.RedefinicaoSenha(Duration.ofMinutes(15))
        );
        verificarESolicitarRedefinicaoSenha = new VerificarESolicitarRedefinicaoSenha(
                usuarioRepository, redefinicaoSenhaRepository, propriedades
        );
    }

    @Test
    void deveLancarExcecaoQuandoCnpjInformadoTemFormatoInvalido() {
        assertThatThrownBy(() -> verificarESolicitarRedefinicaoSenha.verificar("123", "dono@sgtm.local"))
                .isInstanceOf(DadosRedefinicaoNaoConferemException.class);
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontraUsuarioComEmailECnpj() {
        when(usuarioRepository.buscarPorEmailECnpj("dono@sgtm.local", new Cnpj(CNPJ_VALIDO)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> verificarESolicitarRedefinicaoSenha.verificar(CNPJ_VALIDO, "dono@sgtm.local"))
                .isInstanceOf(DadosRedefinicaoNaoConferemException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioEncontradoESuperAdmin() {
        Instant agora = Instant.now();
        Usuario superAdmin = new Usuario(1L, "admin@sgtm.local", "hash", Perfil.SUPER_ADMIN, null, null, true, 0, null, agora, agora);
        when(usuarioRepository.buscarPorEmailECnpj("admin@sgtm.local", new Cnpj(CNPJ_VALIDO)))
                .thenReturn(Optional.of(superAdmin));

        assertThatThrownBy(() -> verificarESolicitarRedefinicaoSenha.verificar(CNPJ_VALIDO, "admin@sgtm.local"))
                .isInstanceOf(DadosRedefinicaoNaoConferemException.class);
    }

    @Test
    void deveGerarTokenQuandoDadosConferem() {
        Instant agora = Instant.now();
        Usuario dono = new Usuario(1L, "dono@sgtm.local", "hash", Perfil.DONO, 10L, new Cnpj(CNPJ_VALIDO), true, 0, null, agora, agora);
        when(usuarioRepository.buscarPorEmailECnpj("dono@sgtm.local", new Cnpj(CNPJ_VALIDO)))
                .thenReturn(Optional.of(dono));

        ResultadoVerificacaoRedefinicao resultado = verificarESolicitarRedefinicaoSenha.verificar(CNPJ_VALIDO, "dono@sgtm.local");

        assertThat(resultado.token()).isNotBlank();
        verify(redefinicaoSenhaRepository).salvar(any(RedefinicaoSenha.class));
    }
}
