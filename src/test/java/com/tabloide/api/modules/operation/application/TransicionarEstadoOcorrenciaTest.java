package com.tabloide.api.modules.operation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.EventoOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
import com.tabloide.api.modules.operation.domain.SeveridadeOcorrencia;
import com.tabloide.api.modules.operation.domain.exceptions.OcorrenciaNaoEncontradaException;
import com.tabloide.api.modules.operation.domain.exceptions.TransicaoEstadoOcorrenciaInvalidaException;
import com.tabloide.api.modules.operation.domain.exceptions.VersaoOcorrenciaDesatualizadaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransicionarEstadoOcorrenciaTest {

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private TransicionarEstadoOcorrencia transicionarEstadoOcorrencia;

    @BeforeEach
    void configurar() {
        transicionarEstadoOcorrencia = new TransicionarEstadoOcorrencia(ocorrenciaRepository, auditoriaRepository);
    }

    private static Ocorrencia ocorrenciaAberta(Long versao) {
        Instant agora = Instant.now();
        return new Ocorrencia(1L, "Título", "Descrição", SeveridadeOcorrencia.ALTA, EstadoOcorrencia.ABERTA, 1L, 2L, agora, null, null, agora, versao);
    }

    @Test
    void deveLancarNaoEncontradaQuandoIdNaoExiste() {
        when(ocorrenciaRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transicionarEstadoOcorrencia.executar(1L, 0L, EstadoOcorrencia.EM_ANALISE, null, 9L, Perfil.SUPER_ADMIN))
                .isInstanceOf(OcorrenciaNaoEncontradaException.class);
    }

    @Test
    void deveLancarVersaoDesatualizadaQuandoVersaoNaoConfere() {
        when(ocorrenciaRepository.buscarPorId(1L)).thenReturn(Optional.of(ocorrenciaAberta(5L)));

        assertThatThrownBy(() -> transicionarEstadoOcorrencia.executar(1L, 4L, EstadoOcorrencia.EM_ANALISE, null, 9L, Perfil.SUPER_ADMIN))
                .isInstanceOf(VersaoOcorrenciaDesatualizadaException.class);

        verify(ocorrenciaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarTransicaoInvalida() {
        when(ocorrenciaRepository.buscarPorId(1L)).thenReturn(Optional.of(ocorrenciaAberta(0L)));

        assertThatThrownBy(() -> transicionarEstadoOcorrencia.executar(1L, 0L, EstadoOcorrencia.RESOLVIDA, null, 9L, Perfil.SUPER_ADMIN))
                .isInstanceOf(TransicaoEstadoOcorrenciaInvalidaException.class);

        verify(ocorrenciaRepository, never()).salvar(any());
    }

    @Test
    void deveTransicionarRegistrarEventoEAuditoria() {
        when(ocorrenciaRepository.buscarPorId(1L)).thenReturn(Optional.of(ocorrenciaAberta(0L)));
        when(ocorrenciaRepository.salvar(any(Ocorrencia.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Ocorrencia resultado = transicionarEstadoOcorrencia.executar(1L, 0L, EstadoOcorrencia.EM_ANALISE, "iniciando análise", 9L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estado()).isEqualTo(EstadoOcorrencia.EM_ANALISE);
        verify(ocorrenciaRepository).registrarEvento(any(EventoOcorrencia.class));
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
