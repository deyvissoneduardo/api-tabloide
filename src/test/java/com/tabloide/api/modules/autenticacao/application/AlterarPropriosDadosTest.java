package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.CredenciaisInvalidasException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.EmailJaCadastradoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.NenhumaAlteracaoInformadaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AlterarPropriosDadosTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AlterarPropriosDados alterarPropriosDados;

    @BeforeEach
    void configurar() {
        alterarPropriosDados = new AlterarPropriosDados(usuarioRepository, sessaoRepository, auditoriaRepository, passwordEncoder);
    }

    private static Usuario usuarioAtivo() {
        Instant agora = Instant.now();
        return new Usuario(USUARIO_ID, "dono@sgtm.local", "hashAtual", Perfil.DONO, 10L, null, true, 0, null, agora, agora);
    }

    @Test
    void deveRejeitarQuandoNenhumaAlteracaoInformada() {
        assertThatThrownBy(() -> alterarPropriosDados.executar(
                USUARIO_ID, new DadosAlteracaoPropriosDados("SenhaAtual1", null, null), Perfil.DONO, 10L))
                .isInstanceOf(NenhumaAlteracaoInformadaException.class);

        verify(usuarioRepository, never()).buscarPorId(any());
    }

    @Test
    void deveRejeitarQuandoSenhaAtualNaoConfere() {
        Usuario usuario = usuarioAtivo();
        when(usuarioRepository.buscarPorId(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaErrada1", "hashAtual")).thenReturn(false);

        assertThatThrownBy(() -> alterarPropriosDados.executar(
                USUARIO_ID, new DadosAlteracaoPropriosDados("SenhaErrada1", null, "NovaSenha1"), Perfil.DONO, 10L))
                .isInstanceOf(CredenciaisInvalidasException.class);

        verify(usuarioRepository, never()).salvar(any());
    }

    @Test
    void deveTrocarSenhaERevogarTodasAsSessoes() {
        Usuario usuario = usuarioAtivo();
        when(usuarioRepository.buscarPorId(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaAtual1", "hashAtual")).thenReturn(true);
        when(passwordEncoder.encode("NovaSenha1")).thenReturn("hashNovo");
        when(usuarioRepository.salvar(usuario)).thenReturn(usuario);

        Usuario resultado = alterarPropriosDados.executar(
                USUARIO_ID, new DadosAlteracaoPropriosDados("SenhaAtual1", null, "NovaSenha1"), Perfil.DONO, 10L);

        assertThat(resultado.senhaHash()).isEqualTo("hashNovo");
        verify(sessaoRepository).revogarTodasDoUsuario(eq(USUARIO_ID), any());
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveTrocarApenasEmailSemRevogarSessoes() {
        Usuario usuario = usuarioAtivo();
        when(usuarioRepository.buscarPorId(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaAtual1", "hashAtual")).thenReturn(true);
        when(usuarioRepository.buscarPorEmail("novo@sgtm.local")).thenReturn(Optional.empty());
        when(usuarioRepository.salvar(usuario)).thenReturn(usuario);

        Usuario resultado = alterarPropriosDados.executar(
                USUARIO_ID, new DadosAlteracaoPropriosDados("SenhaAtual1", "novo@sgtm.local", null), Perfil.DONO, 10L);

        assertThat(resultado.email()).isEqualTo("novo@sgtm.local");
        verify(sessaoRepository, never()).revogarTodasDoUsuario(any(), any());
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveRejeitarEmailJaCadastradoPorOutroUsuario() {
        Usuario usuario = usuarioAtivo();
        Usuario outro = new Usuario(2L, "novo@sgtm.local", "outroHash", Perfil.OPERADOR, 10L, null, true, 0, null, Instant.now(), Instant.now());
        when(usuarioRepository.buscarPorId(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaAtual1", "hashAtual")).thenReturn(true);
        when(usuarioRepository.buscarPorEmail("novo@sgtm.local")).thenReturn(Optional.of(outro));

        assertThatThrownBy(() -> alterarPropriosDados.executar(
                USUARIO_ID, new DadosAlteracaoPropriosDados("SenhaAtual1", "novo@sgtm.local", null), Perfil.DONO, 10L))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(usuarioRepository, never()).salvar(any());
    }
}
