package com.tabloide.api.modules.oferta.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.oferta.domain.EstadoOferta;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.oferta.domain.exceptions.OfertaNaoEncontradaException;
import com.tabloide.api.modules.oferta.domain.exceptions.TransicaoEstadoOfertaInvalidaException;
import java.math.BigDecimal;
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
class AtivarOfertaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long OFERTA_ID = 5L;

    @Mock
    private OfertaRepository ofertaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AtivarOferta ativarOferta;

    @BeforeEach
    void configurar() {
        ativarOferta = new AtivarOferta(ofertaRepository, auditoriaRepository);
    }

    private static Oferta oferta(EstadoOferta estado, Instant inicio, Instant fim) {
        Instant agora = Instant.now();
        return new Oferta(OFERTA_ID, SUPERMERCADO_ID, 10L, Set.of(20L), BigDecimal.TEN, BigDecimal.ONE,
                inicio, fim, null, estado, 0L, agora, agora);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> ativarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 1L, Perfil.DONO, 2L))
                .isInstanceOf(OfertaNaoEncontradaException.class);
    }

    @Test
    void deveAtivarRespeitandoVigencia() {
        Instant agora = Instant.now();
        Oferta desativada = oferta(EstadoOferta.DESATIVADA, agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(desativada));
        when(ofertaRepository.salvar(any(Oferta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Oferta resultado = ativarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estado()).isEqualTo(EstadoOferta.VIGENTE);
        verify(auditoriaRepository).registrar(any());
    }

    @Test
    void deveSerIdempotenteQuandoJaAtiva() {
        Instant agora = Instant.now();
        Oferta vigente = oferta(EstadoOferta.VIGENTE, agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(vigente));

        Oferta resultado = ativarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estado()).isEqualTo(EstadoOferta.VIGENTE);
        verify(ofertaRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }

    @Test
    void naoDeveAtivarQuandoRascunho() {
        Instant agora = Instant.now();
        Oferta rascunho = oferta(EstadoOferta.RASCUNHO, agora, agora.plus(1, ChronoUnit.DAYS));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(rascunho));

        assertThatThrownBy(() -> ativarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(TransicaoEstadoOfertaInvalidaException.class);
    }
}
