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
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CadastrarCategoriaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private CadastrarCategoria cadastrarCategoria;

    @BeforeEach
    void configurar() {
        cadastrarCategoria = new CadastrarCategoria(categoriaRepository, supermercadoRepository, auditoriaRepository);
    }

    private static DadosCategoria dadosValidos() {
        return new DadosCategoria("Bebidas", "Cervejas e refrigerantes");
    }

    private static Supermercado supermercadoAtivo() {
        return new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null,
                EstadoSupermercado.ATIVO, 0L, Instant.now(), Instant.now());
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> cadastrarCategoria.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, 2L))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);

        verify(categoriaRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoSupermercadoBloqueadoOuDesativado() {
        Supermercado bloqueado = new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null,
                EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> cadastrarCategoria.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(categoriaRepository, never()).salvar(any());
    }

    @Test
    void deveCadastrarERegistrarAuditoria() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(categoriaRepository.salvar(any(Categoria.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Categoria resultado = cadastrarCategoria.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isTrue();
        assertThat(resultado.nome()).isEqualTo("Bebidas");
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
