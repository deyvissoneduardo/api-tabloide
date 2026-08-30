package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarSessoesTest {

    @Mock
    private SessaoRepository sessaoRepository;

    private ListarSessoes listarSessoes;

    @BeforeEach
    void configurar() {
        listarSessoes = new ListarSessoes(sessaoRepository);
    }

    @Test
    void deveListarSemFiltroDeSupermercadoQuandoSolicitanteForSuperAdmin() {
        Pagina<SessaoDetalhada> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(sessaoRepository.listar(isNull(), eq(0), eq(25))).thenReturn(pagina);

        Pagina<SessaoDetalhada> resultado = listarSessoes.executar(Perfil.SUPER_ADMIN, 10L, 0, 25);

        assertThat(resultado).isEqualTo(pagina);
        verify(sessaoRepository).listar(isNull(), eq(0), eq(25));
    }

    @Test
    void deveListarComFiltroDoProprioSupermercadoQuandoSolicitanteForDono() {
        Pagina<SessaoDetalhada> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(sessaoRepository.listar(eq(10L), eq(0), eq(25))).thenReturn(pagina);

        listarSessoes.executar(Perfil.DONO, 10L, 0, 25);

        verify(sessaoRepository).listar(eq(10L), eq(0), eq(25));
    }

    @Test
    void deveRejeitarTamanhoDePaginaForaDaListaPermitida() {
        assertThatThrownBy(() -> listarSessoes.executar(Perfil.SUPER_ADMIN, 10L, 0, 10))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }

    @Test
    void deveRejeitarPaginaNegativa() {
        assertThatThrownBy(() -> listarSessoes.executar(Perfil.SUPER_ADMIN, 10L, -1, 25))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }
}
