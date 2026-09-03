package com.tabloide.api.modules.categoria.application;

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
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtivarCategoriaTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AtivarCategoria ativarCategoria;

    @BeforeEach
    void configurar() {
        ativarCategoria = new AtivarCategoria(categoriaRepository, auditoriaRepository);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> ativarCategoria.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, 2L))
                .isInstanceOf(CategoriaNaoEncontradaException.class);

        verify(categoriaRepository, never()).salvar(any());
    }

    @Test
    void deveAtivarCategoriaDesativada() {
        Categoria categoria = new Categoria(1L, SUPERMERCADO_ID, "Bebidas", null, EstadoCategoria.DESATIVADA, 0L, Instant.now(), Instant.now());
        when(categoriaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.salvar(any(Categoria.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Categoria resultado = ativarCategoria.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isTrue();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveSerIdempotenteQuandoJaAtiva() {
        Categoria categoria = new Categoria(1L, SUPERMERCADO_ID, "Bebidas", null, EstadoCategoria.ATIVA, 0L, Instant.now(), Instant.now());
        when(categoriaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(categoria));

        Categoria resultado = ativarCategoria.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isTrue();
        verify(categoriaRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }
}
