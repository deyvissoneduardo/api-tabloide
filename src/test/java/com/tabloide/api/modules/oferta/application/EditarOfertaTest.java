package com.tabloide.api.modules.oferta.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.oferta.domain.EstadoOferta;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.oferta.domain.exceptions.OfertaNaoEncontradaException;
import com.tabloide.api.modules.oferta.domain.exceptions.TransicaoEstadoOfertaInvalidaException;
import com.tabloide.api.modules.oferta.domain.exceptions.VersaoOfertaDesatualizadaException;
import com.tabloide.api.modules.produto.domain.EstadoProduto;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
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
class EditarOfertaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long OFERTA_ID = 5L;
    private static final Long PRODUTO_ID = 10L;
    private static final Long LOJA_ID = 20L;

    @Mock
    private OfertaRepository ofertaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private EditarOferta editarOferta;

    @BeforeEach
    void configurar() {
        editarOferta = new EditarOferta(ofertaRepository, produtoRepository, lojaRepository, supermercadoRepository, auditoriaRepository);
    }

    private static Supermercado supermercadoAtivo() {
        Instant agora = Instant.now();
        return new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null, null, null, null,
                EstadoSupermercado.ATIVO, 0L, agora, agora);
    }

    private static Produto produtoNoSupermercado() {
        Instant agora = Instant.now();
        return new Produto(PRODUTO_ID, SUPERMERCADO_ID, "Produto", Set.of(1L), null, null, null, null, null,
                EstadoProduto.ATIVO, 0L, agora, agora);
    }

    private static Loja lojaNoSupermercado() {
        Instant agora = Instant.now();
        return new Loja(LOJA_ID, SUPERMERCADO_ID, "Loja Centro", "loja centro", null, null, EstadoLoja.ATIVA, 0L, agora, agora);
    }

    private static Oferta ofertaRascunho() {
        Instant agora = Instant.now();
        return new Oferta(OFERTA_ID, SUPERMERCADO_ID, PRODUTO_ID, Set.of(LOJA_ID), BigDecimal.TEN, BigDecimal.ONE,
                agora, agora.plus(1, ChronoUnit.DAYS), null, EstadoOferta.RASCUNHO, 0L, agora, agora);
    }

    private static DadosOferta novosDados() {
        Instant agora = Instant.now();
        return new DadosOferta(PRODUTO_ID, Set.of(LOJA_ID), new BigDecimal("20.00"), new BigDecimal("15.00"), agora, agora.plus(2, ChronoUnit.DAYS), "Nova condição", false);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> editarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 0L, novosDados(), 1L, Perfil.DONO, 2L))
                .isInstanceOf(OfertaNaoEncontradaException.class);
    }

    @Test
    void deveRejeitarQuandoNaoEncontrada() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 0L, novosDados(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(OfertaNaoEncontradaException.class);
    }

    @Test
    void deveRejeitarQuandoVersaoDesatualizada() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(ofertaRascunho()));

        assertThatThrownBy(() -> editarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 99L, novosDados(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(VersaoOfertaDesatualizadaException.class);
    }

    @Test
    void deveRejeitarQuandoFinalizada() {
        Instant agora = Instant.now();
        Oferta cancelada = new Oferta(OFERTA_ID, SUPERMERCADO_ID, PRODUTO_ID, Set.of(LOJA_ID), BigDecimal.TEN, BigDecimal.ONE,
                agora, agora.plus(1, ChronoUnit.DAYS), null, EstadoOferta.CANCELADA, 0L, agora, agora);
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(cancelada));
        when(produtoRepository.buscarPorIdESupermercado(PRODUTO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(produtoNoSupermercado()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));

        assertThatThrownBy(() -> editarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 0L, novosDados(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(TransicaoEstadoOfertaInvalidaException.class);
    }

    @Test
    void deveEditarERegistrarAuditoria() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(ofertaRepository.buscarPorIdESupermercado(OFERTA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(ofertaRascunho()));
        when(produtoRepository.buscarPorIdESupermercado(PRODUTO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(produtoNoSupermercado()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));
        when(ofertaRepository.salvar(any(Oferta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Oferta resultado = editarOferta.executar(SUPERMERCADO_ID, OFERTA_ID, 0L, novosDados(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.precoNormal()).isEqualByComparingTo("20.00");
        assertThat(resultado.condicoes()).isEqualTo("Nova condição");
        verify(auditoriaRepository).registrar(any());
    }
}
