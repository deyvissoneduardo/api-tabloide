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
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DesativarCategoriaTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    private DesativarCategoria desativarCategoria;

    @BeforeEach
    void configurar() {
        desativarCategoria = new DesativarCategoria(categoriaRepository, auditoriaRepository, supermercadoRepository);
        org.mockito.Mockito.lenient().when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(
                new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null,
                        null, null, null, EstadoSupermercado.ATIVO, 0L, Instant.now(), Instant.now())));
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> desativarCategoria.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, 2L))
                .isInstanceOf(CategoriaNaoEncontradaException.class);

        verify(categoriaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoSupermercadoEstaBloqueado() {
        Supermercado bloqueado = new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999",
                null, null, null, null, EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> desativarCategoria.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(categoriaRepository, never()).salvar(any());
    }

    @Test
    void deveDesativarCategoriaAtiva() {
        Categoria categoria = new Categoria(1L, SUPERMERCADO_ID, "Bebidas", null, EstadoCategoria.ATIVA, 0L, Instant.now(), Instant.now());
        when(categoriaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.salvar(any(Categoria.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Categoria resultado = desativarCategoria.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isFalse();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveSerIdempotenteQuandoJaDesativada() {
        Categoria categoria = new Categoria(1L, SUPERMERCADO_ID, "Bebidas", null, EstadoCategoria.DESATIVADA, 0L, Instant.now(), Instant.now());
        when(categoriaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(categoria));

        Categoria resultado = desativarCategoria.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isFalse();
        verify(categoriaRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }
}
