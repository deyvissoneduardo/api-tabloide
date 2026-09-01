package com.tabloide.api.modules.imagem.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.ImagemRepository;
import com.tabloide.api.modules.imagem.domain.exceptions.ImagemNaoEncontradaException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarImagensTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private ImagemRepository imagemRepository;

    private ListarImagens listarImagens;

    @BeforeEach
    void configurar() {
        listarImagens = new ListarImagens(imagemRepository);
    }

    @Test
    void deveRejeitarDonoForaDoEscopo() {
        assertThatThrownBy(() -> listarImagens.executar(SUPERMERCADO_ID, Perfil.DONO, 2L, null, 0, 25))
                .isInstanceOf(ImagemNaoEncontradaException.class);
    }

    @Test
    void devePermitirSuperAdminForaDoEscopo() {
        Pagina<Imagem> paginaVazia = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(imagemRepository.listarPorSupermercado(SUPERMERCADO_ID, null, 0, 25)).thenReturn(paginaVazia);

        Pagina<Imagem> resultado = listarImagens.executar(SUPERMERCADO_ID, Perfil.SUPER_ADMIN, 999L, null, 0, 25);

        assertThat(resultado.itens()).isEmpty();
    }

    @Test
    void deveRejeitarTamanhoDePaginaInvalido() {
        assertThatThrownBy(() -> listarImagens.executar(SUPERMERCADO_ID, Perfil.DONO, SUPERMERCADO_ID, null, 0, 10))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }
}
