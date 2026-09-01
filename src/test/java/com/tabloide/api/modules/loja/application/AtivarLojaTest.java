package com.tabloide.api.modules.loja.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtivarLojaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AtivarLoja ativarLoja;

    @BeforeEach
    void configurar() {
        ativarLoja = new AtivarLoja(lojaRepository, auditoriaRepository);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> ativarLoja.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, 2L))
                .isInstanceOf(LojaNaoEncontradaException.class);

        verify(lojaRepository, never()).salvar(any());
    }

    @Test
    void deveAtivarLojaDesativada() {
        Loja loja = new Loja(1L, SUPERMERCADO_ID, "Loja", "loja", ENDERECO, null, EstadoLoja.DESATIVADA, 0L, Instant.now(), Instant.now());
        when(lojaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(loja));
        when(lojaRepository.salvar(any(Loja.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Loja resultado = ativarLoja.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isTrue();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveSerIdempotenteQuandoJaAtiva() {
        Loja loja = new Loja(1L, SUPERMERCADO_ID, "Loja", "loja", ENDERECO, null, EstadoLoja.ATIVA, 0L, Instant.now(), Instant.now());
        when(lojaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(loja));

        Loja resultado = ativarLoja.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isTrue();
        verify(lojaRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }
}
