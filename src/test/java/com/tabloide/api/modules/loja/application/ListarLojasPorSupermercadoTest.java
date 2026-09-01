package com.tabloide.api.modules.loja.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarLojasPorSupermercadoTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private LojaRepository lojaRepository;

    private ListarLojasPorSupermercado listarLojasPorSupermercado;

    @BeforeEach
    void configurar() {
        listarLojasPorSupermercado = new ListarLojasPorSupermercado(lojaRepository);
    }

    @Test
    void deveRejeitarDonoForaDoEscopo() {
        assertThatThrownBy(() -> listarLojasPorSupermercado.executar(SUPERMERCADO_ID, Perfil.DONO, 2L, 0, 25))
                .isInstanceOf(LojaNaoEncontradaException.class);
    }

    @Test
    void devePermitirSuperAdminForaDoEscopo() {
        Pagina<Loja> paginaVazia = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(lojaRepository.listarPorSupermercado(SUPERMERCADO_ID, 0, 25)).thenReturn(paginaVazia);

        Pagina<Loja> resultado = listarLojasPorSupermercado.executar(SUPERMERCADO_ID, Perfil.SUPER_ADMIN, 999L, 0, 25);

        assertThat(resultado.itens()).isEmpty();
    }

    @Test
    void deveRejeitarTamanhoDePaginaInvalido() {
        assertThatThrownBy(() -> listarLojasPorSupermercado.executar(SUPERMERCADO_ID, Perfil.DONO, SUPERMERCADO_ID, 0, 10))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }

    @Test
    void deveRejeitarPaginaNegativa() {
        assertThatThrownBy(() -> listarLojasPorSupermercado.executar(SUPERMERCADO_ID, Perfil.DONO, SUPERMERCADO_ID, -1, 25))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }
}
