package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.AcessoNegadoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.EmailJaCadastradoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.PerfilInvalidoParaCadastroException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioAdministrativoTest {

    private static final Long SUPERMERCADO_ID = 10L;
    private static final Long ATOR_ID = 1L;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CadastrarUsuarioAdministrativo construir() {
        return new CadastrarUsuarioAdministrativo(supermercadoRepository, usuarioRepository, auditoriaRepository, passwordEncoder);
    }

    private Supermercado supermercadoAtivo() {
        return supermercado(EstadoSupermercado.ATIVO);
    }

    private Supermercado supermercado(EstadoSupermercado estado) {
        Instant agora = Instant.now();
        return new Supermercado(
                SUPERMERCADO_ID,
                new Cnpj("11222333000181"),
                "Razão Social LTDA",
                "Mercado Bom Preço",
                "contato@mercado.com",
                "11999998888",
                new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP"),
                null,
                null,
                null,
                estado,
                1L,
                agora,
                agora
        );
    }

    @Test
    void deveCadastrarOperadorQuandoDadosValidos() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        Supermercado supermercado = supermercadoAtivo();
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("novo@sgtm.local", "SenhaValida1", Perfil.OPERADOR);

        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercado));
        when(usuarioRepository.buscarPorEmail("novo@sgtm.local")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SenhaValida1")).thenReturn("hash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenAnswer(chamada -> {
            Usuario usuario = chamada.getArgument(0);
            return new Usuario(99L, usuario.email(), usuario.senhaHash(), usuario.perfil(), usuario.supermercadoId(),
                    usuario.supermercadoCnpj(), usuario.estaAtivo(), usuario.tentativasLoginInvalidas(),
                    usuario.bloqueadoAte(), usuario.criadoEm(), usuario.atualizadoEm());
        });

        Usuario salvo = cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(salvo.id()).isEqualTo(99L);
        assertThat(salvo.perfil()).isEqualTo(Perfil.OPERADOR);
        verify(auditoriaRepository).registrar(any());
    }

    @Test
    void deveCadastrarDonoQuandoDadosValidos() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        Supermercado supermercado = supermercadoAtivo();
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("outro-dono@sgtm.local", "SenhaValida1", Perfil.DONO);

        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercado));
        when(usuarioRepository.buscarPorEmail("outro-dono@sgtm.local")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SenhaValida1")).thenReturn("hash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Usuario salvo = cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(salvo.perfil()).isEqualTo(Perfil.DONO);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        Supermercado supermercado = supermercadoAtivo();
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("existente@sgtm.local", "SenhaValida1", Perfil.OPERADOR);

        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercado));
        when(usuarioRepository.buscarPorEmail("existente@sgtm.local"))
                .thenReturn(Optional.of(new Usuario(5L, "existente@sgtm.local", "hash", Perfil.OPERADOR, SUPERMERCADO_ID, null, true, 0, null, Instant.now(), Instant.now())));

        assertThatThrownBy(() -> cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(usuarioRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }

    @Test
    void deveLancarExcecaoQuandoPerfilForSuperAdminSemConsultarSupermercadoOuEmail() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("qualquer@sgtm.local", "SenhaValida1", Perfil.SUPER_ADMIN);

        assertThatThrownBy(() -> cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(PerfilInvalidoParaCadastroException.class);

        verify(supermercadoRepository, never()).buscarPorId(any());
        verify(usuarioRepository, never()).buscarPorEmail(any());
    }

    @Test
    void deveLancarExcecaoQuandoSupermercadoBloqueado() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        Supermercado supermercado = supermercado(EstadoSupermercado.BLOQUEADO);
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("novo@sgtm.local", "SenhaValida1", Perfil.OPERADOR);

        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercado));

        assertThatThrownBy(() -> cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoSupermercadoDesativado() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        Supermercado supermercado = supermercado(EstadoSupermercado.DESATIVADO);
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("novo@sgtm.local", "SenhaValida1", Perfil.OPERADOR);

        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercado));

        assertThatThrownBy(() -> cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoSupermercadoNaoExiste() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("novo@sgtm.local", "SenhaValida1", Perfil.OPERADOR);

        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoAtorForaDoEscopoSemConsultarSupermercado() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("novo@sgtm.local", "SenhaValida1", Perfil.OPERADOR);
        Long supermercadoIdDoAtor = 20L;

        assertThatThrownBy(() -> cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.DONO, supermercadoIdDoAtor))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);

        verify(supermercadoRepository, never()).buscarPorId(any());
    }

    @Test
    void superAdminDeveCadastrarPrimeiroDonoEmQualquerSupermercadoAtivo() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        Supermercado supermercado = supermercadoAtivo();
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("primeiro-dono@sgtm.local", "SenhaValida1", Perfil.DONO);

        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercado));
        when(usuarioRepository.buscarPorEmail("primeiro-dono@sgtm.local")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SenhaValida1")).thenReturn("hash");
        when(usuarioRepository.salvar(any(Usuario.class))).thenAnswer(chamada -> chamada.getArgument(0));

        // Super Admin não pertence a nenhum supermercado (supermercadoIdAtor null).
        Usuario salvo = cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.SUPER_ADMIN, null);

        assertThat(salvo.perfil()).isEqualTo(Perfil.DONO);
        verify(auditoriaRepository).registrar(any());
    }

    @Test
    void superAdminNaoDeveCadastrarOperadorSemConsultarSupermercadoOuEmail() {
        CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo = construir();
        DadosNovoUsuarioAdministrativo dados = new DadosNovoUsuarioAdministrativo("operador@sgtm.local", "SenhaValida1", Perfil.OPERADOR);

        assertThatThrownBy(() -> cadastrarUsuarioAdministrativo.executar(SUPERMERCADO_ID, dados, ATOR_ID, Perfil.SUPER_ADMIN, null))
                .isInstanceOf(AcessoNegadoException.class);

        verify(supermercadoRepository, never()).buscarPorId(any());
        verify(usuarioRepository, never()).buscarPorEmail(any());
    }
}
