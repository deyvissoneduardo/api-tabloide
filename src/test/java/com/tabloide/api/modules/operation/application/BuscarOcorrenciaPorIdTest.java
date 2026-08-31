package com.tabloide.api.modules.operation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.EventoOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
import com.tabloide.api.modules.operation.domain.SeveridadeOcorrencia;
import com.tabloide.api.modules.operation.domain.exceptions.OcorrenciaNaoEncontradaException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarOcorrenciaPorIdTest {

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    private BuscarOcorrenciaPorId buscarOcorrenciaPorId;

    @BeforeEach
    void configurar() {
        buscarOcorrenciaPorId = new BuscarOcorrenciaPorId(ocorrenciaRepository);
    }

    @Test
    void deveLancarNaoEncontradaQuandoIdNaoExiste() {
        when(ocorrenciaRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarOcorrenciaPorId.executar(1L)).isInstanceOf(OcorrenciaNaoEncontradaException.class);
    }

    @Test
    void deveRetornarOcorrenciaComHistorico() {
        Instant agora = Instant.now();
        Ocorrencia ocorrencia = new Ocorrencia(1L, "Título", "Descrição", SeveridadeOcorrencia.ALTA, EstadoOcorrencia.ABERTA, 1L, 2L, agora, null, null, agora, 0L);
        EventoOcorrencia evento = EventoOcorrencia.transicao(1L, null, EstadoOcorrencia.ABERTA, null, 9L, agora);
        when(ocorrenciaRepository.buscarPorId(1L)).thenReturn(Optional.of(ocorrencia));
        when(ocorrenciaRepository.listarEventos(1L)).thenReturn(List.of(evento));

        OcorrenciaComHistorico resultado = buscarOcorrenciaPorId.executar(1L);

        assertThat(resultado.ocorrencia()).isEqualTo(ocorrencia);
        assertThat(resultado.historico()).containsExactly(evento);
    }
}
