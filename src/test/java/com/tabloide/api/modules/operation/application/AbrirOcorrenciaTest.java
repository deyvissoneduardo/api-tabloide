package com.tabloide.api.modules.operation.application;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbrirOcorrenciaTest {

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AbrirOcorrencia abrirOcorrencia;

    @BeforeEach
    void configurar() {
        abrirOcorrencia = new AbrirOcorrencia(ocorrenciaRepository, auditoriaRepository);
    }

    @Test
    void deveAbrirComEstadoAbertaERegistrarEventoEAuditoria() {
        when(ocorrenciaRepository.salvar(any(Ocorrencia.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        DadosAbrirOcorrencia dados = new DadosAbrirOcorrencia("Título", "Descrição", SeveridadeOcorrencia.ALTA, 1L, null);
        Ocorrencia resultado = abrirOcorrencia.executar(dados, 9L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estado()).isEqualTo(EstadoOcorrencia.ABERTA);
        assertThat(resultado.responsavelId()).isEqualTo(9L);
        verify(ocorrenciaRepository).registrarEvento(any(EventoOcorrencia.class));
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveUsarResponsavelInformadoQuandoPresente() {
        when(ocorrenciaRepository.salvar(any(Ocorrencia.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        DadosAbrirOcorrencia dados = new DadosAbrirOcorrencia("Título", "Descrição", SeveridadeOcorrencia.BAIXA, 1L, 42L);
        Ocorrencia resultado = abrirOcorrencia.executar(dados, 9L, Perfil.SUPER_ADMIN);

        assertThat(resultado.responsavelId()).isEqualTo(42L);
    }
}
