package com.tabloide.api.modules.oferta.application;

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
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.produto.domain.EstadoProduto;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.produto.domain.exceptions.ProdutoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
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
class CadastrarOfertaTest {

    private static final Long SUPERMERCADO_ID = 1L;
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

    private CadastrarOferta cadastrarOferta;

    @BeforeEach
    void configurar() {
        cadastrarOferta = new CadastrarOferta(ofertaRepository, produtoRepository, lojaRepository, supermercadoRepository, auditoriaRepository);
    }

    private static DadosOferta dadosValidos() {
        Instant agora = Instant.now();
        return new DadosOferta(
                PRODUTO_ID, Set.of(LOJA_ID), new BigDecimal("10.00"), new BigDecimal("7.50"),
                agora, agora.plus(1, ChronoUnit.DAYS), null, false
        );
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
        return new Loja(LOJA_ID, SUPERMERCADO_ID, "Loja Centro", "loja centro", null, null,
                com.tabloide.api.modules.loja.domain.EstadoLoja.ATIVA, 0L, agora, agora);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> cadastrarOferta.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, 2L))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);

        verify(ofertaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoSupermercadoBloqueadoOuDesativado() {
        Instant agora = Instant.now();
        Supermercado bloqueado = new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null, null, null, null,
                EstadoSupermercado.BLOQUEADO, 0L, agora, agora);
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> cadastrarOferta.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(ofertaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoProdutoNaoExisteNoSupermercado() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(produtoRepository.buscarPorIdESupermercado(PRODUTO_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cadastrarOferta.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(ProdutoNaoEncontradoException.class);

        verify(ofertaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoLojaNaoExisteNoSupermercado() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(produtoRepository.buscarPorIdESupermercado(PRODUTO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(produtoNoSupermercado()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cadastrarOferta.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(LojaNaoEncontradaException.class);

        verify(ofertaRepository, never()).salvar(any());
    }

    @Test
    void deveCadastrarERegistrarAuditoria() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(produtoRepository.buscarPorIdESupermercado(PRODUTO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(produtoNoSupermercado()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));
        when(ofertaRepository.salvar(any(Oferta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Oferta resultado = cadastrarOferta.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.produtoId()).isEqualTo(PRODUTO_ID);
        assertThat(resultado.lojaIds()).containsExactly(LOJA_ID);
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
