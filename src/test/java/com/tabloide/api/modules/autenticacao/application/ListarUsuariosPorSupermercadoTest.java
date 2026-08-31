package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarUsuariosPorSupermercadoTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private ListarUsuariosPorSupermercado listarUsuariosPorSupermercado;

    @BeforeEach
    void configurar() {
        listarUsuariosPorSupermercado = new ListarUsuariosPorSupermercado(usuarioRepository);
    }

    @Test
    void deveListarComPaginacaoValida() {
        Pagina<Usuario> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(usuarioRepository.listarPorSupermercado(eq(1L), eq(0), eq(25))).thenReturn(pagina);

        Pagina<Usuario> resultado = listarUsuariosPorSupermercado.executar(1L, 0, 25);

        assertThat(resultado).isEqualTo(pagina);
    }

    @Test
    void deveRejeitarTamanhoDePaginaForaDaListaPermitida() {
        assertThatThrownBy(() -> listarUsuariosPorSupermercado.executar(1L, 0, 10))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }
}
