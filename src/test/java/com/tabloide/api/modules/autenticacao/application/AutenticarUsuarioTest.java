package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Sessao;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.CredenciaisInvalidasException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AutenticarUsuarioTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AutenticarUsuario autenticarUsuario;

    @BeforeEach
    void configurar() {
        AutenticacaoProperties propriedades = new AutenticacaoProperties(
                new AutenticacaoProperties.Sessao(Duration.ofHours(8), Duration.ofMinutes(30)),
                new AutenticacaoProperties.Login(5, Duration.ofMinutes(15)),
                new AutenticacaoProperties.RedefinicaoSenha(Duration.ofMinutes(15))
        );
        autenticarUsuario = new AutenticarUsuario(usuarioRepository, sessaoRepository, passwordEncoder, propriedades);
    }

    private static Usuario usuarioAtivo() {
        Instant agora = Instant.now();
        return new Usuario(1L, "dono@sgtm.local", "hash", Perfil.DONO, 10L, null, true, 0, null, agora, agora);
    }

    @Test
    void deveLancarCredenciaisInvalidasQuandoUsuarioNaoExiste() {
        when(usuarioRepository.buscarPorEmail("inexistente@sgtm.local")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autenticarUsuario.autenticar("inexistente@sgtm.local", "qualquer"))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void deveLancarCredenciaisInvalidasQuandoSenhaNaoConfere() {
        Usuario usuario = usuarioAtivo();
        when(usuarioRepository.buscarPorEmail(usuario.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("errada", usuario.senhaHash())).thenReturn(false);

        assertThatThrownBy(() -> autenticarUsuario.autenticar(usuario.email(), "errada"))
                .isInstanceOf(CredenciaisInvalidasException.class);

        verify(usuarioRepository).salvar(usuario);
        assertThat(usuario.tentativasLoginInvalidas()).isEqualTo(1);
    }

    @Test
    void deveBloquearUsuarioAposCincoTentativasInvalidas() {
        Usuario usuario = usuarioAtivo();
        when(usuarioRepository.buscarPorEmail(usuario.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("errada", usuario.senhaHash())).thenReturn(false);

        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> autenticarUsuario.autenticar(usuario.email(), "errada"))
                    .isInstanceOf(CredenciaisInvalidasException.class);
        }

        assertThat(usuario.bloqueadoAte()).isNotNull();
    }

    @Test
    void deveLancarCredenciaisInvalidasQuandoUsuarioInativo() {
        Instant agora = Instant.now();
        Usuario inativo = new Usuario(1L, "dono@sgtm.local", "hash", Perfil.DONO, 10L, null, false, 0, null, agora, agora);
        when(usuarioRepository.buscarPorEmail(inativo.email())).thenReturn(Optional.of(inativo));

        assertThatThrownBy(() -> autenticarUsuario.autenticar(inativo.email(), "qualquer"))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void deveLancarCredenciaisInvalidasQuandoUsuarioBloqueadoTemporariamente() {
        Instant agora = Instant.now();
        Usuario bloqueado = new Usuario(1L, "dono@sgtm.local", "hash", Perfil.DONO, 10L, null, true, 5, agora.plus(Duration.ofMinutes(10)), agora, agora);
        when(usuarioRepository.buscarPorEmail(bloqueado.email())).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> autenticarUsuario.autenticar(bloqueado.email(), "qualquer"))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void deveCriarSessaoEZerarTentativasQuandoCredenciaisCorretas() {
        Usuario usuario = usuarioAtivo();
        when(usuarioRepository.buscarPorEmail(usuario.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("correta", usuario.senhaHash())).thenReturn(true);

        SessaoAutenticada resultado = autenticarUsuario.autenticar(usuario.email(), "correta");

        assertThat(resultado.usuario()).isEqualTo(usuario);
        assertThat(resultado.sessao().usuarioId()).isEqualTo(usuario.id());
        assertThat(usuario.tentativasLoginInvalidas()).isZero();
        verify(sessaoRepository).salvar(any(Sessao.class));
    }
}
