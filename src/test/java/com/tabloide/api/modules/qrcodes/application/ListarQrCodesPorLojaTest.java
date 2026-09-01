package com.tabloide.api.modules.qrcodes.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarQrCodesPorLojaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long LOJA_ID = 2L;

    @Mock
    private QrCodeRepository qrCodeRepository;

    private ListarQrCodesPorLoja listarQrCodesPorLoja;

    @BeforeEach
    void configurar() {
        listarQrCodesPorLoja = new ListarQrCodesPorLoja(qrCodeRepository);
    }

    @Test
    void deveRejeitarDonoForaDoEscopo() {
        assertThatThrownBy(() -> listarQrCodesPorLoja.executar(SUPERMERCADO_ID, LOJA_ID, Perfil.DONO, 2L, 0, 25))
                .isInstanceOf(LojaNaoEncontradaException.class);
    }

    @Test
    void devePermitirSuperAdminForaDoEscopo() {
        Pagina<QrCode> paginaVazia = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(qrCodeRepository.listarPorLoja(LOJA_ID, SUPERMERCADO_ID, 0, 25)).thenReturn(paginaVazia);

        Pagina<QrCode> resultado = listarQrCodesPorLoja.executar(SUPERMERCADO_ID, LOJA_ID, Perfil.SUPER_ADMIN, 999L, 0, 25);

        assertThat(resultado.itens()).isEmpty();
    }

    @Test
    void deveRejeitarTamanhoDePaginaInvalido() {
        assertThatThrownBy(() -> listarQrCodesPorLoja.executar(SUPERMERCADO_ID, LOJA_ID, Perfil.DONO, SUPERMERCADO_ID, 0, 10))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }

    @Test
    void deveRejeitarPaginaNegativa() {
        assertThatThrownBy(() -> listarQrCodesPorLoja.executar(SUPERMERCADO_ID, LOJA_ID, Perfil.DONO, SUPERMERCADO_ID, -1, 25))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }
}
