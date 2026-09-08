package com.tabloide.api.modules.produto.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.domain.CategoriaRepository;
import com.tabloide.api.modules.categoria.domain.EstadoCategoria;
import com.tabloide.api.modules.produto.domain.EstadoProduto;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.produto.domain.exceptions.VersaoProdutoDesatualizadaException;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssociarCategoriasAoProdutoTest {
    @Mock ProdutoRepository produtoRepository;
    @Mock CategoriaRepository categoriaRepository;
    @Mock SupermercadoRepository supermercadoRepository;
    @Mock AuditoriaRepository auditoriaRepository;
    private AssociarCategoriasAoProduto casoDeUso;

    @BeforeEach
    void configurar() {
        casoDeUso = new AssociarCategoriasAoProduto(produtoRepository, categoriaRepository,
                supermercadoRepository, auditoriaRepository);
    }

    @Test
    void deveSubstituirCategoriasERegistrarAuditoria() {
        Instant agora = Instant.now();
        Supermercado supermercado = new Supermercado(1L, null, "Razão", "Fantasia", "e@e.com", "119999",
                null, null, null, null, EstadoSupermercado.ATIVO, 0L, agora, agora);
        Produto produto = new Produto(5L, 1L, "Produto", Set.of(10L), null, null, null, null, null,
                EstadoProduto.ATIVO, 2L, agora, agora);
        Categoria categoria = new Categoria(20L, 1L, "Nova", null, EstadoCategoria.ATIVA, 0L, agora, agora);
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado));
        when(produtoRepository.buscarPorIdESupermercado(5L, 1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.buscarPorIdESupermercado(20L, 1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        Produto resultado = casoDeUso.executar(1L, 5L, 2L, Set.of(20L), 7L, Perfil.DONO, 1L);

        assertThat(resultado.categoriaIds()).containsExactly(20L);
        verify(auditoriaRepository).registrar(any());
    }

    @Test
    void deveRejeitarVersaoDesatualizada() {
        Instant agora = Instant.now();
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(new Supermercado(
                1L, null, "Razão", "Fantasia", "e@e.com", "119999", null, null, null, null,
                EstadoSupermercado.ATIVO, 0L, agora, agora)));
        when(produtoRepository.buscarPorIdESupermercado(5L, 1L)).thenReturn(Optional.of(new Produto(
                5L, 1L, "Produto", Set.of(10L), null, null, null, null, null,
                EstadoProduto.ATIVO, 2L, agora, agora)));

        assertThatThrownBy(() -> casoDeUso.executar(1L, 5L, 1L, Set.of(20L), 7L, Perfil.DONO, 1L))
                .isInstanceOf(VersaoProdutoDesatualizadaException.class);
    }
}
