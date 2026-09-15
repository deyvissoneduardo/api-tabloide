package com.tabloide.api.modules.campanha.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.EstadoCampanha;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancelarCampanhaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long CAMPANHA_ID = 5L;

    @Mock
    private CampanhaRepository campanhaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private CancelarCampanha cancelarCampanha;

    @BeforeEach
    void configurar() {
        cancelarCampanha = new CancelarCampanha(campanhaRepository, auditoriaRepository);
    }

    private static Campanha campanha(EstadoCampanha estado) {
        Instant agora = Instant.now();
        return new Campanha(CAMPANHA_ID, SUPERMERCADO_ID, "Campanha", null, Set.of(10L), Set.of(),
                agora, agora.plus(1, ChronoUnit.DAYS), estado, 0L, agora, agora);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> cancelarCampanha.executar(SUPERMERCADO_ID, CAMPANHA_ID, 1L, Perfil.DONO, 2L))
                .isInstanceOf(CampanhaNaoEncontradaException.class);
    }

    @Test
    void deveCancelarERegistrarAuditoria() {
        when(campanhaRepository.buscarPorIdESupermercado(CAMPANHA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(campanha(EstadoCampanha.RASCUNHO)));
        when(campanhaRepository.salvar(any(Campanha.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Campanha resultado = cancelarCampanha.executar(SUPERMERCADO_ID, CAMPANHA_ID, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estado()).isEqualTo(EstadoCampanha.CANCELADA);
        verify(auditoriaRepository).registrar(any());
    }

    @Test
    void deveSerIdempotenteQuandoJaFinalizada() {
        when(campanhaRepository.buscarPorIdESupermercado(CAMPANHA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(campanha(EstadoCampanha.CANCELADA)));

        Campanha resultado = cancelarCampanha.executar(SUPERMERCADO_ID, CAMPANHA_ID, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estado()).isEqualTo(EstadoCampanha.CANCELADA);
        verify(campanhaRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }
}
