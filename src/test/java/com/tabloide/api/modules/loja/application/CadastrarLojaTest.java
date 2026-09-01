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
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LimiteDeLojasDoPlanoAtingidoException;
import com.tabloide.api.modules.loja.domain.exceptions.NomeDeLojaJaCadastradoException;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CadastrarLojaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AssinaturaRepository assinaturaRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private CadastrarLoja cadastrarLoja;

    @BeforeEach
    void configurar() {
        cadastrarLoja = new CadastrarLoja(lojaRepository, supermercadoRepository, assinaturaRepository, planoRepository, auditoriaRepository);
    }

    private static DadosLoja dadosValidos() {
        return new DadosLoja("Loja Centro", ENDERECO, null);
    }

    private static Supermercado supermercadoAtivo() {
        return new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null,
                EstadoSupermercado.ATIVO, 0L, Instant.now(), Instant.now());
    }

    private static Assinatura assinaturaVigente(Long planoId) {
        return new Assinatura(1L, SUPERMERCADO_ID, planoId, "Plano", 30, BigDecimal.TEN, 100,
                EstadoAssinatura.VIGENTE, Instant.now(), Instant.now().plusSeconds(3600), Instant.now());
    }

    private static Plano planoComLimite(Integer limiteLojas) {
        return new Plano(10L, "Plano", "plano", 30, BigDecimal.TEN, 100, limiteLojas, 0L, Instant.now(), Instant.now(), null);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> cadastrarLoja.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, 2L))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);

        verify(lojaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoSupermercadoBloqueadoOuDesativado() {
        Supermercado bloqueado = new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null,
                EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> cadastrarLoja.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(lojaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarNomeDuplicadoNoSupermercado() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.existeNomeNormalizadoNoSupermercado("loja centro", SUPERMERCADO_ID)).thenReturn(true);

        assertThatThrownBy(() -> cadastrarLoja.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(NomeDeLojaJaCadastradoException.class);

        verify(lojaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoLimiteDoPlanoAtingido() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.existeNomeNormalizadoNoSupermercado("loja centro", SUPERMERCADO_ID)).thenReturn(false);
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(SUPERMERCADO_ID))
                .thenReturn(Optional.of(assinaturaVigente(10L)));
        when(planoRepository.buscarPorId(10L)).thenReturn(Optional.of(planoComLimite(2)));
        when(lojaRepository.contarAtivasPorSupermercado(SUPERMERCADO_ID)).thenReturn(2L);

        assertThatThrownBy(() -> cadastrarLoja.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(LimiteDeLojasDoPlanoAtingidoException.class);

        verify(lojaRepository, never()).salvar(any());
    }

    @Test
    void devePermitirCadastroSemLimiteQuandoPlanoNaoDefineLimiteLojas() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.existeNomeNormalizadoNoSupermercado("loja centro", SUPERMERCADO_ID)).thenReturn(false);
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(SUPERMERCADO_ID))
                .thenReturn(Optional.of(assinaturaVigente(10L)));
        when(planoRepository.buscarPorId(10L)).thenReturn(Optional.of(planoComLimite(null)));
        when(lojaRepository.salvar(any(Loja.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Loja resultado = cadastrarLoja.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isTrue();
        verify(lojaRepository, never()).contarAtivasPorSupermercado(any());
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveRejeitarQuandoNaoHaAssinaturaVigente() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.existeNomeNormalizadoNoSupermercado("loja centro", SUPERMERCADO_ID)).thenReturn(false);
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cadastrarLoja.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(AssinaturaNaoEncontradaException.class);

        verify(lojaRepository, never()).salvar(any());
    }

    @Test
    void deveCadastrarERegistrarAuditoriaDentroDoLimite() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.existeNomeNormalizadoNoSupermercado("loja centro", SUPERMERCADO_ID)).thenReturn(false);
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(SUPERMERCADO_ID))
                .thenReturn(Optional.of(assinaturaVigente(10L)));
        when(planoRepository.buscarPorId(10L)).thenReturn(Optional.of(planoComLimite(5)));
        when(lojaRepository.contarAtivasPorSupermercado(SUPERMERCADO_ID)).thenReturn(1L);
        when(lojaRepository.salvar(any(Loja.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Loja resultado = cadastrarLoja.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isTrue();
        assertThat(resultado.nome()).isEqualTo("Loja Centro");
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
