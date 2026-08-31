package com.tabloide.api.modules.plano.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.AvisoInterno;
import com.tabloide.api.modules.plano.domain.AvisoInternoRepository;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.supermercado.application.BloquearSupermercado;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessarVencimentoAssinaturasTest {

    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");

    @Mock
    private AssinaturaRepository assinaturaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AvisoInternoRepository avisoInternoRepository;

    @Mock
    private BloquearSupermercado bloquearSupermercado;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private ProcessarVencimentoAssinaturas processarVencimentoAssinaturas;

    @BeforeEach
    void configurar() {
        processarVencimentoAssinaturas = new ProcessarVencimentoAssinaturas(
                assinaturaRepository, usuarioRepository, avisoInternoRepository, bloquearSupermercado, auditoriaRepository
        );
        lenient().when(assinaturaRepository.listarVigentesComVencimentoAte(any())).thenReturn(List.of());
        lenient().when(assinaturaRepository.listarVigentesComVencimentoEntre(any(), any())).thenReturn(List.of());
    }

    private static Plano plano(Long id, String nome) {
        Instant agora = Instant.now();
        return new Plano(id, nome, Plano.normalizarNome(nome), 30, BigDecimal.valueOf(99.90), 100, null, 0L, agora, agora, null);
    }

    private static Usuario usuarioDono(Long id, Long supermercadoId) {
        Instant agora = Instant.now();
        return new Usuario(id, "dono" + id + "@sgtm.local", "hash", Perfil.DONO, supermercadoId, null, true, 0, null, agora, agora);
    }

    @Test
    void deveMarcarVencidaBloquearSupermercadoEAuditar() {
        Assinatura vigente = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        when(assinaturaRepository.listarVigentesComVencimentoAte(any())).thenReturn(List.of(vigente));
        when(assinaturaRepository.salvar(any(Assinatura.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        processarVencimentoAssinaturas.executar();

        assertThat(vigente.estado()).isEqualTo(EstadoAssinatura.VENCIDA);
        verify(bloquearSupermercado).executar(eq(1L), isNull(), eq(Perfil.SUPER_ADMIN));
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveGerarUmAvisoPorDonoAtivoParaAssinaturaVencendoEmDezDias() {
        Assinatura vencendoEm10Dias = Assinatura.associar(2L, plano(6L, "Premium"), Instant.now());
        Instant agora = Instant.now();
        LocalDate dataAlvo10Dias = agora.atZone(FUSO_BRASILIA).toLocalDate().plusDays(10);
        when(assinaturaRepository.listarVigentesComVencimentoEntre(any(), any())).thenAnswer(invocacao -> {
            Instant inicio = invocacao.getArgument(0);
            LocalDate dataConsultada = inicio.atZone(FUSO_BRASILIA).toLocalDate();
            return dataConsultada.equals(dataAlvo10Dias) ? List.of(vencendoEm10Dias) : List.<Assinatura>of();
        });
        when(usuarioRepository.listarAtivosPorSupermercadoEPerfil(2L, Perfil.DONO))
                .thenReturn(List.of(usuarioDono(10L, 2L), usuarioDono(11L, 2L)));
        when(avisoInternoRepository.salvarSeNaoExiste(any(AvisoInterno.class))).thenReturn(true);

        processarVencimentoAssinaturas.executar();

        verify(avisoInternoRepository, times(2)).salvarSeNaoExiste(any(AvisoInterno.class));
        verify(bloquearSupermercado, never()).executar(any(), any(), any());
    }

    @Test
    void naoDeveFalharQuandoAvisoJaExiste() {
        Assinatura vencendo = Assinatura.associar(3L, plano(7L, "Básico"), Instant.now());
        Instant agora = Instant.now();
        LocalDate dataAlvo1Dia = agora.atZone(FUSO_BRASILIA).toLocalDate().plusDays(1);
        when(assinaturaRepository.listarVigentesComVencimentoEntre(any(), any())).thenAnswer(invocacao -> {
            Instant inicio = invocacao.getArgument(0);
            LocalDate dataConsultada = inicio.atZone(FUSO_BRASILIA).toLocalDate();
            return dataConsultada.equals(dataAlvo1Dia) ? List.of(vencendo) : List.<Assinatura>of();
        });
        when(usuarioRepository.listarAtivosPorSupermercadoEPerfil(3L, Perfil.DONO)).thenReturn(List.of(usuarioDono(20L, 3L)));
        when(avisoInternoRepository.salvarSeNaoExiste(any(AvisoInterno.class))).thenReturn(false);

        processarVencimentoAssinaturas.executar();

        verify(avisoInternoRepository).salvarSeNaoExiste(any(AvisoInterno.class));
    }
}
