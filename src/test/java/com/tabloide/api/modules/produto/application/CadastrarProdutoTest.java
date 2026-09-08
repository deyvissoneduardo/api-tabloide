package com.tabloide.api.modules.produto.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.CategoriaRepository;
import com.tabloide.api.modules.categoria.domain.EstadoCategoria;
import com.tabloide.api.modules.categoria.domain.exceptions.CategoriaNaoEncontradaException;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.categoria.domain.exceptions.CategoriaDesativadaNaoAceitaAssociacaoException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CadastrarProdutoTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long CATEGORIA_ID = 10L;
    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private CadastrarProduto cadastrarProduto;

    @BeforeEach
    void configurar() {
        cadastrarProduto = new CadastrarProduto(produtoRepository, categoriaRepository, supermercadoRepository, auditoriaRepository);
    }

    private static DadosProduto dadosValidos() {
        return new DadosProduto("Refrigerante Cola 2L", Set.of(CATEGORIA_ID), "Marca X", null, null, null, null);
    }

    private static Supermercado supermercadoAtivo() {
        return new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null,
                EstadoSupermercado.ATIVO, 0L, Instant.now(), Instant.now());
    }

    private static Categoria categoriaAtiva() {
        return new Categoria(CATEGORIA_ID, SUPERMERCADO_ID, "Bebidas", null, EstadoCategoria.ATIVA, 0L, Instant.now(), Instant.now());
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> cadastrarProduto.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, 2L))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);

        verify(produtoRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoSupermercadoBloqueadoOuDesativado() {
        Supermercado bloqueado = new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null,
                EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> cadastrarProduto.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(produtoRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoCategoriaNaoExisteNoSupermercado() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(categoriaRepository.buscarPorIdESupermercado(CATEGORIA_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cadastrarProduto.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(CategoriaNaoEncontradaException.class);

        verify(produtoRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoCategoriaEstaDesativada() {
        Categoria categoriaDesativada = new Categoria(CATEGORIA_ID, SUPERMERCADO_ID, "Bebidas", null, EstadoCategoria.DESATIVADA, 0L, Instant.now(), Instant.now());
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(categoriaRepository.buscarPorIdESupermercado(CATEGORIA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(categoriaDesativada));

        assertThatThrownBy(() -> cadastrarProduto.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(CategoriaDesativadaNaoAceitaAssociacaoException.class);

        verify(produtoRepository, never()).salvar(any());
    }

    @Test
    void deveCadastrarERegistrarAuditoria() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(categoriaRepository.buscarPorIdESupermercado(CATEGORIA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(categoriaAtiva()));
        when(produtoRepository.salvar(any(Produto.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Produto resultado = cadastrarProduto.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtivo()).isTrue();
        assertThat(resultado.nome()).isEqualTo("Refrigerante Cola 2L");
        assertThat(resultado.categoriaIds()).containsExactly(CATEGORIA_ID);
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
