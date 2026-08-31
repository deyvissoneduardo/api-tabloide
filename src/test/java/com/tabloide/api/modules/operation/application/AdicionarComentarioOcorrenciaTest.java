package com.tabloide.api.modules.operation.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdicionarComentarioOcorrenciaTest {

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AdicionarComentarioOcorrencia adicionarComentarioOcorrencia;

    @BeforeEach
    void configurar() {
        adicionarComentarioOcorrencia = new AdicionarComentarioOcorrencia(ocorrenciaRepository, auditoriaRepository);
    }

    @Test
    void deveLancarNaoEncontradaQuandoIdNaoExiste() {
        when(ocorrenciaRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adicionarComentarioOcorrencia.executar(1L, "comentário", 9L, Perfil.SUPER_ADMIN))
                .isInstanceOf(OcorrenciaNaoEncontradaException.class);
    }

    @Test
    void deveRegistrarEventoDeComentarioSemAlterarEstado() {
        Instant agora = Instant.now();
        Ocorrencia ocorrencia = new Ocorrencia(1L, "Título", "Descrição", SeveridadeOcorrencia.ALTA, EstadoOcorrencia.ABERTA, 1L, 2L, agora, null, null, agora, 0L);
        when(ocorrenciaRepository.buscarPorId(1L)).thenReturn(Optional.of(ocorrencia));
        when(ocorrenciaRepository.salvar(any(Ocorrencia.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        adicionarComentarioOcorrencia.executar(1L, "comentário", 9L, Perfil.SUPER_ADMIN);

        verify(ocorrenciaRepository).registrarEvento(any(EventoOcorrencia.class));
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
