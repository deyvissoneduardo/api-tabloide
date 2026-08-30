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
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.TransicaoEstadoInvalidaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BloquearSupermercadoTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private BloquearSupermercado bloquearSupermercado;

    @BeforeEach
    void configurar() {
        bloquearSupermercado = new BloquearSupermercado(supermercadoRepository, auditoriaRepository);
    }

    private static Supermercado supermercado(EstadoSupermercado estado) {
        Instant agora = Instant.now();
        return new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, estado, 0L, agora, agora);
    }

    @Test
    void deveLancarNaoEncontradoQuandoIdNaoExiste() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bloquearSupermercado.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);
    }

    @Test
    void deveBloquearQuandoAtivo() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(supermercadoRepository.salvar(any(Supermercado.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Supermercado resultado = bloquearSupermercado.executar(1L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estaBloqueado()).isTrue();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveSerIdempotenteQuandoJaBloqueado() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.BLOQUEADO)));

        Supermercado resultado = bloquearSupermercado.executar(1L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estaBloqueado()).isTrue();
        verify(supermercadoRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }

    @Test
    void deveRejeitarBloqueioQuandoDesativado() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.DESATIVADO)));

        assertThatThrownBy(() -> bloquearSupermercado.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }
}
