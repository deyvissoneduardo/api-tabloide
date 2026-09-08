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
import com.tabloide.api.modules.categoria.domain.exceptions.VersaoDesatualizadaException;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EditarCategoriaTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    private EditarCategoria editarCategoria;

    @BeforeEach
    void configurar() {
        editarCategoria = new EditarCategoria(categoriaRepository, auditoriaRepository, supermercadoRepository);
        org.mockito.Mockito.lenient().when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(
                new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null,
                        null, null, null, EstadoSupermercado.ATIVO, 0L, Instant.now(), Instant.now())));
    }

    private static Categoria categoriaExistente() {
        return new Categoria(1L, SUPERMERCADO_ID, "Bebidas", null, EstadoCategoria.ATIVA, 0L, Instant.now(), Instant.now());
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> editarCategoria.executar(SUPERMERCADO_ID, 1L, 0L, new DadosCategoria("Bebidas", null), 1L, Perfil.DONO, 2L))
                .isInstanceOf(CategoriaNaoEncontradaException.class);

        verify(categoriaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoVersaoDesatualizada() {
        when(categoriaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(categoriaExistente()));

        assertThatThrownBy(() -> editarCategoria.executar(SUPERMERCADO_ID, 1L, 99L, new DadosCategoria("Bebidas", null), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(VersaoDesatualizadaException.class);

        verify(categoriaRepository, never()).salvar(any());
    }

    @Test
    void deveEditarERegistrarAuditoria() {
        when(categoriaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(categoriaExistente()));
        when(categoriaRepository.salvar(any(Categoria.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Categoria resultado = editarCategoria.executar(
                SUPERMERCADO_ID, 1L, 0L, new DadosCategoria("Bebidas Geladas", "Nova descrição"), 1L, Perfil.DONO, SUPERMERCADO_ID
        );

        assertThat(resultado.nome()).isEqualTo("Bebidas Geladas");
        assertThat(resultado.descricao()).isEqualTo("Nova descrição");
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
