package com.tabloide.api.modules.supermercado.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarSupermercadosTest {

    @Mock
    private SupermercadoRepository supermercadoRepository;

    private ListarSupermercados listarSupermercados;

    @BeforeEach
    void configurar() {
        listarSupermercados = new ListarSupermercados(supermercadoRepository);
    }

    @Test
    void deveListarComPaginacaoValida() {
        Pagina<Supermercado> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(supermercadoRepository.listar(eq(0), eq(25))).thenReturn(pagina);

        Pagina<Supermercado> resultado = listarSupermercados.executar(0, 25);

        assertThat(resultado).isEqualTo(pagina);
    }

    @Test
    void deveRejeitarTamanhoDePaginaForaDaListaPermitida() {
        assertThatThrownBy(() -> listarSupermercados.executar(0, 10))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }

    @Test
    void deveRejeitarPaginaNegativa() {
        assertThatThrownBy(() -> listarSupermercados.executar(-1, 25))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }
}
