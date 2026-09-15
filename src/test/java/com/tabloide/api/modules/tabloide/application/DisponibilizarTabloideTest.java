package com.tabloide.api.modules.tabloide.application;

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
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import com.tabloide.api.modules.tabloide.domain.Tabloide;
import com.tabloide.api.modules.tabloide.domain.TabloideRepository;
import com.tabloide.api.modules.tabloide.domain.TipoArquivoTabloide;
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
class DisponibilizarTabloideTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long LOJA_ID = 20L;

    @Mock
    private TabloideRepository tabloideRepository;

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private DisponibilizarTabloide disponibilizarTabloide;

    @BeforeEach
    void configurar() {
        disponibilizarTabloide = new DisponibilizarTabloide(tabloideRepository, lojaRepository, supermercadoRepository, auditoriaRepository);
    }

    private static DadosTabloide dadosValidos() {
        Instant agora = Instant.now();
        return new DadosTabloide(
                "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf", 1024L,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS)
        );
    }

    private static Supermercado supermercadoAtivo() {
        Instant agora = Instant.now();
        return new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null, null, null, null,
                EstadoSupermercado.ATIVO, 0L, agora, agora);
    }

    private static Loja lojaNoSupermercado() {
        Instant agora = Instant.now();
        return new Loja(LOJA_ID, SUPERMERCADO_ID, "Loja Centro", "loja centro", null, null, EstadoLoja.ATIVA, 0L, agora, agora);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> disponibilizarTabloide.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, 2L))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);

        verify(tabloideRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoSupermercadoBloqueadoOuDesativado() {
        Instant agora = Instant.now();
        Supermercado bloqueado = new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null, null, null, null,
                EstadoSupermercado.BLOQUEADO, 0L, agora, agora);
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> disponibilizarTabloide.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(tabloideRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoLojaNaoExisteNoSupermercado() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disponibilizarTabloide.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(LojaNaoEncontradaException.class);

        verify(tabloideRepository, never()).salvar(any());
    }

    @Test
    void deveDisponibilizarERegistrarAuditoria() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));
        when(tabloideRepository.salvar(any(Tabloide.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Tabloide resultado = disponibilizarTabloide.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.titulo()).isEqualTo("Ofertas da semana");
        assertThat(resultado.lojaIds()).containsExactly(LOJA_ID);
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
