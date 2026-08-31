package com.tabloide.api.modules.auditoria.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.FiltroAuditoria;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarAuditoriaTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private ListarAuditoria listarAuditoria;

    private static final FiltroAuditoria FILTRO_VAZIO = new FiltroAuditoria(null, null, null, null, null, null);

    @BeforeEach
    void configurar() {
        listarAuditoria = new ListarAuditoria(auditoriaRepository);
    }

    @Test
    void deveListarSemFiltroDeSupermercadoQuandoSolicitanteForSuperAdmin() {
        Pagina<RegistroAuditoria> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(auditoriaRepository.listar(isNull(), eq(FILTRO_VAZIO), eq(0), eq(25))).thenReturn(pagina);

        Pagina<RegistroAuditoria> resultado = listarAuditoria.executar(Perfil.SUPER_ADMIN, 10L, FILTRO_VAZIO, 0, 25);

        assertThat(resultado).isEqualTo(pagina);
        verify(auditoriaRepository).listar(isNull(), eq(FILTRO_VAZIO), eq(0), eq(25));
    }

    @Test
    void deveListarComFiltroDoProprioSupermercadoQuandoSolicitanteForDono() {
        Pagina<RegistroAuditoria> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(auditoriaRepository.listar(eq(10L), eq(FILTRO_VAZIO), eq(0), eq(25))).thenReturn(pagina);

        listarAuditoria.executar(Perfil.DONO, 10L, FILTRO_VAZIO, 0, 25);

        verify(auditoriaRepository).listar(eq(10L), eq(FILTRO_VAZIO), eq(0), eq(25));
    }

    @Test
    void deveListarComFiltroDeSupermercadoEspecificoQuandoSolicitanteForSuperAdmin() {
        FiltroAuditoria filtroComSupermercado = new FiltroAuditoria(null, null, null, null, null, 99L);
        Pagina<RegistroAuditoria> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(auditoriaRepository.listar(eq(99L), eq(filtroComSupermercado), eq(0), eq(25))).thenReturn(pagina);

        listarAuditoria.executar(Perfil.SUPER_ADMIN, 10L, filtroComSupermercado, 0, 25);

        verify(auditoriaRepository).listar(eq(99L), eq(filtroComSupermercado), eq(0), eq(25));
    }

    @Test
    void deveIgnorarFiltroDeSupermercadoDeOutroTenantQuandoSolicitanteForDono() {
        FiltroAuditoria filtroComOutroSupermercado = new FiltroAuditoria(null, null, null, null, null, 99L);
        Pagina<RegistroAuditoria> pagina = new Pagina<>(List.of(), 0, 25, 0, 0);
        when(auditoriaRepository.listar(eq(10L), eq(filtroComOutroSupermercado), eq(0), eq(25))).thenReturn(pagina);

        listarAuditoria.executar(Perfil.DONO, 10L, filtroComOutroSupermercado, 0, 25);

        verify(auditoriaRepository).listar(eq(10L), eq(filtroComOutroSupermercado), eq(0), eq(25));
    }

    @Test
    void deveRejeitarTamanhoDePaginaForaDaListaPermitida() {
        assertThatThrownBy(() -> listarAuditoria.executar(Perfil.SUPER_ADMIN, 10L, FILTRO_VAZIO, 0, 10))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }

    @Test
    void deveRejeitarPaginaNegativa() {
        assertThatThrownBy(() -> listarAuditoria.executar(Perfil.SUPER_ADMIN, 10L, FILTRO_VAZIO, -1, 25))
                .isInstanceOf(TamanhoPaginaInvalidoException.class);
    }
}
