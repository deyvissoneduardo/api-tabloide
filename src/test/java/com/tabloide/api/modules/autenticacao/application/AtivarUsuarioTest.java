package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.UsuarioNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtivarUsuarioTest {

    private static final Long SUPERMERCADO_ID = 10L;
    private static final Long USUARIO_ID = 5L;
    private static final Long ATOR_ID = 1L;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AtivarUsuario construir() {
        return new AtivarUsuario(usuarioRepository, supermercadoRepository, auditoriaRepository);
    }

    private Usuario usuarioInativo() {
        Instant agora = Instant.now();
        return new Usuario(USUARIO_ID, "operador@sgtm.local", "hash", Perfil.OPERADOR, SUPERMERCADO_ID, null, false, 0, null, agora, agora);
    }

    private Supermercado supermercadoAtivo() {
        Instant agora = Instant.now();
        return new Supermercado(
                SUPERMERCADO_ID,
                new com.tabloide.api.modules.autenticacao.domain.Cnpj("11222333000181"),
                "Razão Social LTDA",
                "Mercado Bom Preço",
                "contato@mercado.com",
                "11999998888",
                new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP"),
                null,
                null,
                null,
                EstadoSupermercado.ATIVO,
                1L,
                agora,
                agora
        );
    }

    @Test
    void deveAtivarUsuarioInativo() {
        Usuario usuario = usuarioInativo();
        when(usuarioRepository.buscarPorIdESupermercado(USUARIO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(usuario));
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(usuarioRepository.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario resultado = construir().executar(SUPERMERCADO_ID, USUARIO_ID, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtivo()).isTrue();
        verify(auditoriaRepository).registrar(any());
    }

    @Test
    void deveAtivarComoSuperAdminEmQualquerSupermercado() {
        Usuario usuario = usuarioInativo();
        when(usuarioRepository.buscarPorIdESupermercado(USUARIO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(usuario));
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(usuarioRepository.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario resultado = construir().executar(SUPERMERCADO_ID, USUARIO_ID, ATOR_ID, Perfil.SUPER_ADMIN, null);

        assertThat(resultado.estaAtivo()).isTrue();
    }

    @Test
    void deveSerIdempotenteQuandoUsuarioJaEstaAtivo() {
        Instant agora = Instant.now();
        Usuario ativo = new Usuario(USUARIO_ID, "operador@sgtm.local", "hash", Perfil.OPERADOR, SUPERMERCADO_ID, null, true, 0, null, agora, agora);
        when(usuarioRepository.buscarPorIdESupermercado(USUARIO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(ativo));

        Usuario resultado = construir().executar(SUPERMERCADO_ID, USUARIO_ID, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtivo()).isTrue();
        verify(usuarioRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }

    @Test
    void deveRejeitarQuandoUsuarioForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> construir().executar(SUPERMERCADO_ID, USUARIO_ID, ATOR_ID, Perfil.DONO, 999L))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(usuarioRepository, never()).buscarPorIdESupermercado(any(), any());
    }

    @Test
    void deveRejeitarQuandoUsuarioPertenceAOutroSupermercado() {
        when(usuarioRepository.buscarPorIdESupermercado(USUARIO_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> construir().executar(SUPERMERCADO_ID, USUARIO_ID, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }

    @Test
    void deveRejeitarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.buscarPorIdESupermercado(USUARIO_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> construir().executar(SUPERMERCADO_ID, USUARIO_ID, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }

    @Test
    void deveRejeitarQuandoSupermercadoEstaBloqueadoOuDesativado() {
        Usuario usuario = usuarioInativo();
        when(usuarioRepository.buscarPorIdESupermercado(USUARIO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(usuario));
        Supermercado desativado = new Supermercado(
                SUPERMERCADO_ID,
                new com.tabloide.api.modules.autenticacao.domain.Cnpj("11222333000181"),
                "Razão Social LTDA",
                "Mercado Bom Preço",
                "contato@mercado.com",
                "11999998888",
                new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP"),
                null,
                null,
                null,
                EstadoSupermercado.DESATIVADO,
                1L,
                Instant.now(),
                Instant.now()
        );
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(desativado));

        assertThatThrownBy(() -> construir().executar(SUPERMERCADO_ID, USUARIO_ID, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(usuarioRepository, never()).salvar(any());
    }
}
