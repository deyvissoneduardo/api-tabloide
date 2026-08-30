package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenha;
import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenhaRepository;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.TokenOpaco;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TokenRedefinicaoInvalidoOuExpiradoException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RedefinirSenhaTest {

    @Mock
    private RedefinicaoSenhaRepository redefinicaoSenhaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RedefinirSenha redefinirSenha;

    private RedefinirSenha construir() {
        return new RedefinirSenha(redefinicaoSenhaRepository, usuarioRepository, sessaoRepository, passwordEncoder);
    }

    @Test
    void deveLancarExcecaoQuandoTokenNaoExiste() {
        redefinirSenha = construir();
        when(redefinicaoSenhaRepository.buscarPorTokenHash(TokenOpaco.hash("token-bruto"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> redefinirSenha.redefinir("token-bruto", "NovaSenha1"))
                .isInstanceOf(TokenRedefinicaoInvalidoOuExpiradoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoTokenJaFoiUsado() {
        redefinirSenha = construir();
        Instant agora = Instant.now();
        RedefinicaoSenha redefinicao = RedefinicaoSenha.solicitar(1L, TokenOpaco.hash("token-bruto"), agora, Duration.ofMinutes(15));
        redefinicao.marcarComoUsado(agora);
        when(redefinicaoSenhaRepository.buscarPorTokenHash(TokenOpaco.hash("token-bruto"))).thenReturn(Optional.of(redefinicao));

        assertThatThrownBy(() -> redefinirSenha.redefinir("token-bruto", "NovaSenha1"))
                .isInstanceOf(TokenRedefinicaoInvalidoOuExpiradoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoTokenExpirado() {
        redefinirSenha = construir();
        Instant criadoHaUmaHora = Instant.now().minus(Duration.ofHours(1));
        RedefinicaoSenha redefinicao = RedefinicaoSenha.solicitar(1L, TokenOpaco.hash("token-bruto"), criadoHaUmaHora, Duration.ofMinutes(15));
        when(redefinicaoSenhaRepository.buscarPorTokenHash(TokenOpaco.hash("token-bruto"))).thenReturn(Optional.of(redefinicao));

        assertThatThrownBy(() -> redefinirSenha.redefinir("token-bruto", "NovaSenha1"))
                .isInstanceOf(TokenRedefinicaoInvalidoOuExpiradoException.class);
    }

    @Test
    void deveTrocarSenhaERevogarSessoesQuandoTokenValido() {
        redefinirSenha = construir();
        Instant agora = Instant.now();
        RedefinicaoSenha redefinicao = RedefinicaoSenha.solicitar(1L, TokenOpaco.hash("token-bruto"), agora, Duration.ofMinutes(15));
        Usuario usuario = new Usuario(1L, "dono@sgtm.local", "hashAntigo", Perfil.DONO, 10L, null, true, 0, null, agora, agora);

        when(redefinicaoSenhaRepository.buscarPorTokenHash(TokenOpaco.hash("token-bruto"))).thenReturn(Optional.of(redefinicao));
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("NovaSenha1")).thenReturn("hashNovo");

        redefinirSenha.redefinir("token-bruto", "NovaSenha1");

        assertThat(usuario.senhaHash()).isEqualTo("hashNovo");
        assertThat(redefinicao.usadoEm()).isNotNull();
        verify(usuarioRepository).salvar(usuario);
        verify(redefinicaoSenhaRepository).salvar(redefinicao);
        verify(sessaoRepository).revogarTodasDoUsuario(1L, redefinicao.usadoEm());
    }
}
