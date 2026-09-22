package com.tabloide.api.modules.supermercado.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.VersaoDesatualizadaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EditarSupermercadoTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private EditarSupermercado editarSupermercado;

    @BeforeEach
    void configurar() {
        editarSupermercado = new EditarSupermercado(supermercadoRepository, auditoriaRepository);
    }

    private static Supermercado supermercadoExistente(EstadoSupermercado estado, Long versao) {
        Instant agora = Instant.now();
        return new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, estado, versao, agora, agora);
    }

    private static DadosSupermercado novosDados() {
        return new DadosSupermercado("Nova Razão", "Novo Fantasia", "novo@mercado.com", "11888887777", ENDERECO, "Sala 2", null, null);
    }

    @Test
    void deveLancarNaoEncontradoQuandoIdNaoExiste() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editarSupermercado.executar(1L, 0L, novosDados(), 1L, Perfil.SUPER_ADMIN, null))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);
    }

    @Test
    void deveLancarVersaoDesatualizadaQuandoVersaoNaoConfere() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercadoExistente(EstadoSupermercado.ATIVO, 5L)));

        assertThatThrownBy(() -> editarSupermercado.executar(1L, 4L, novosDados(), 1L, Perfil.SUPER_ADMIN, null))
                .isInstanceOf(VersaoDesatualizadaException.class);

        verify(supermercadoRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarEdicaoQuandoBloqueado() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercadoExistente(EstadoSupermercado.BLOQUEADO, 0L)));

        assertThatThrownBy(() -> editarSupermercado.executar(1L, 0L, novosDados(), 1L, Perfil.SUPER_ADMIN, null))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void deveEditarERegistrarAuditoriaComAntesEDepois() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercadoExistente(EstadoSupermercado.ATIVO, 0L)));
        when(supermercadoRepository.salvar(any(Supermercado.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Supermercado resultado = editarSupermercado.executar(1L, 0L, novosDados(), 1L, Perfil.SUPER_ADMIN, null);

        assertThat(resultado.razaoSocial()).isEqualTo("Nova Razão");
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void donoDeveEditarOProprioSupermercado() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercadoExistente(EstadoSupermercado.ATIVO, 0L)));
        when(supermercadoRepository.salvar(any(Supermercado.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Supermercado resultado = editarSupermercado.executar(1L, 0L, novosDados(), 10L, Perfil.DONO, 1L);

        assertThat(resultado.razaoSocial()).isEqualTo("Nova Razão");
    }

    @Test
    void donoNaoDeveEditarSupermercadoDeOutroId() {
        assertThatThrownBy(() -> editarSupermercado.executar(1L, 0L, novosDados(), 10L, Perfil.DONO, 2L))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);

        verify(supermercadoRepository, never()).buscarPorId(any());
    }
}
