package com.tabloide.api.modules.campanha.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.exceptions.OfertaJaVinculadaAOutraCampanhaException;
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.oferta.domain.EstadoOferta;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.oferta.domain.exceptions.OfertaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
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
class CadastrarCampanhaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long LOJA_ID = 10L;
    private static final Long OFERTA_ID = 20L;

    @Mock
    private CampanhaRepository campanhaRepository;

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private OfertaRepository ofertaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private CadastrarCampanha cadastrarCampanha;

    @BeforeEach
    void configurar() {
        cadastrarCampanha = new CadastrarCampanha(campanhaRepository, lojaRepository, ofertaRepository, supermercadoRepository, auditoriaRepository);
    }

    private static DadosCampanha dadosValidos(Set<Long> ofertaIds) {
        Instant agora = Instant.now();
        return new DadosCampanha("Campanha", null, Set.of(LOJA_ID), ofertaIds, agora, agora.plus(1, ChronoUnit.DAYS), false);
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

    private static Oferta ofertaNoSupermercado() {
        Instant agora = Instant.now();
        return new Oferta(OFERTA_ID, SUPERMERCADO_ID, 5L, Set.of(LOJA_ID), BigDecimal.TEN, BigDecimal.ONE,
                agora, agora.plus(1, ChronoUnit.DAYS), null, EstadoOferta.RASCUNHO, 0L, agora, agora);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> cadastrarCampanha.executar(SUPERMERCADO_ID, dadosValidos(Set.of()), 1L, Perfil.DONO, 2L))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);

        verify(campanhaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoLojaNaoExisteNoSupermercado() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cadastrarCampanha.executar(SUPERMERCADO_ID, dadosValidos(Set.of()), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(LojaNaoEncontradaException.class);

        verify(campanhaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoOfertaNaoExisteNoSupermercado() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cadastrarCampanha.executar(SUPERMERCADO_ID, dadosValidos(Set.of(OFERTA_ID)), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(OfertaNaoEncontradaException.class);

        verify(campanhaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoOfertaJaVinculadaAOutraCampanhaAtiva() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(ofertaNoSupermercado()));
        when(campanhaRepository.existeOfertaEmCampanhaAtiva(SUPERMERCADO_ID, OFERTA_ID, null)).thenReturn(true);

        assertThatThrownBy(() -> cadastrarCampanha.executar(SUPERMERCADO_ID, dadosValidos(Set.of(OFERTA_ID)), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(OfertaJaVinculadaAOutraCampanhaException.class);

        verify(campanhaRepository, never()).salvar(any());
    }

    @Test
    void deveCadastrarERegistrarAuditoria() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));
        when(campanhaRepository.salvar(any(Campanha.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Campanha resultado = cadastrarCampanha.executar(SUPERMERCADO_ID, dadosValidos(Set.of()), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.lojaIds()).containsExactly(LOJA_ID);
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
