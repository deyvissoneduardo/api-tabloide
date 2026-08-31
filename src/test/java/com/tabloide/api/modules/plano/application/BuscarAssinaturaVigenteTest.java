package com.tabloide.api.modules.plano.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarAssinaturaVigenteTest {

    @Mock
    private AssinaturaRepository assinaturaRepository;

    private BuscarAssinaturaVigente buscarAssinaturaVigente;

    @BeforeEach
    void configurar() {
        buscarAssinaturaVigente = new BuscarAssinaturaVigente(assinaturaRepository);
    }

    @Test
    void deveLancarNaoEncontradaQuandoSupermercadoNaoPossuiAssinatura() {
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarAssinaturaVigente.executar(1L)).isInstanceOf(AssinaturaNaoEncontradaException.class);
    }

    @Test
    void deveRetornarAssinaturaVigente() {
        Instant agora = Instant.now();
        Assinatura assinatura = new Assinatura(1L, 1L, 2L, "Plano Ouro", 30, BigDecimal.TEN, 100, EstadoAssinatura.VIGENTE, agora, agora, agora);
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(1L)).thenReturn(Optional.of(assinatura));

        assertThat(buscarAssinaturaVigente.executar(1L)).isEqualTo(assinatura);
    }
}
