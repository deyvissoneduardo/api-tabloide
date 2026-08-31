package com.tabloide.api.modules.operation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.operation.domain.FiltroOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarOcorrenciasTest {

    private static final FiltroOcorrencia FILTRO_VAZIO = new FiltroOcorrencia(null, null, null);

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    private ListarOcorrencias listarOcorrencias;

    @BeforeEach
    void configurar() {
        listarOcorrencias = new ListarOcorrencias(ocorrenciaRepository);
    }

    @Test
    void deveListarComPaginacaoValida() {
        Pagina<Ocorrencia> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(ocorrenciaRepository.listar(eq(FILTRO_VAZIO), eq(0), eq(25))).thenReturn(pagina);

        Pagina<Ocorrencia> resultado = listarOcorrencias.executar(FILTRO_VAZIO, 0, 25);

        assertThat(resultado).isEqualTo(pagina);
    }

    @Test
    void deveRejeitarTamanhoDePaginaForaDaListaPermitida() {
        assertThatThrownBy(() -> listarOcorrencias.executar(FILTRO_VAZIO, 0, 10))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }
}
