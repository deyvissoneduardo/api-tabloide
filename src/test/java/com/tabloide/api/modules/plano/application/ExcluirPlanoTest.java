package com.tabloide.api.modules.plano.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExcluirPlanoTest {

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private ExcluirPlano excluirPlano;

    @BeforeEach
    void configurar() {
        excluirPlano = new ExcluirPlano(planoRepository, auditoriaRepository);
    }

    private static Plano plano(Instant excluidoEm) {
        Instant agora = Instant.now();
        return new Plano(1L, "Básico", Plano.normalizarNome("Básico"), 30, BigDecimal.valueOf(99.90), 100, null, 0L, agora, agora, excluidoEm);
    }

    @Test
    void deveExcluirEAuditarQuandoPlanoAindaNaoExcluido() {
        when(planoRepository.buscarPorId(1L)).thenReturn(Optional.of(plano(null)));
        when(planoRepository.salvar(any(Plano.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Plano resultado = excluirPlano.executar(1L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estaExcluido()).isTrue();
        verify(auditoriaRepository, times(1)).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveSerIdempotenteQuandoPlanoJaExcluido() {
        Instant exclusaoAnterior = Instant.now().minusSeconds(3600);
        when(planoRepository.buscarPorId(1L)).thenReturn(Optional.of(plano(exclusaoAnterior)));

        Plano resultado = excluirPlano.executar(1L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.excluidoEm()).isEqualTo(exclusaoAnterior);
        verify(planoRepository, never()).salvar(any(Plano.class));
        verify(auditoriaRepository, never()).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveLancarNaoEncontradoQuandoPlanoNaoExiste() {
        when(planoRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> excluirPlano.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(PlanoNaoEncontradoException.class);
    }
}
