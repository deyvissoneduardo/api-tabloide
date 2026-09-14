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
import com.tabloide.api.modules.produto.domain.EstadoProduto;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.produto.domain.exceptions.ProdutoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DesativarProdutoTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    private DesativarProduto desativarProduto;

    @BeforeEach
    void configurar() {
        desativarProduto = new DesativarProduto(produtoRepository, auditoriaRepository, supermercadoRepository);
        org.mockito.Mockito.lenient().when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(
                new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null,
                        null, null, null, EstadoSupermercado.ATIVO, 0L, Instant.now(), Instant.now())));
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> desativarProduto.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, 2L))
                .isInstanceOf(ProdutoNaoEncontradoException.class);

        verify(produtoRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoSupermercadoEstaBloqueado() {
        Supermercado bloqueado = new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999",
                null, null, null, null, EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> desativarProduto.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(produtoRepository, never()).salvar(any());
    }

    @Test
    void deveDesativarProdutoAtivo() {
        Produto produto = new Produto(1L, SUPERMERCADO_ID, "Produto", Set.of(10L), null, null, null, null, null,
                EstadoProduto.ATIVO, 0L, Instant.now(), Instant.now());
        when(produtoRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(produto));
        when(produtoRepository.salvar(any(Produto.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Produto resultado = desativarProduto.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtivo()).isFalse();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveSerIdempotenteQuandoJaDesativado() {
        Produto produto = new Produto(1L, SUPERMERCADO_ID, "Produto", Set.of(10L), null, null, null, null, null,
                EstadoProduto.DESATIVADO, 0L, Instant.now(), Instant.now());
        when(produtoRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(produto));

        Produto resultado = desativarProduto.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtivo()).isFalse();
        verify(produtoRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }
}
