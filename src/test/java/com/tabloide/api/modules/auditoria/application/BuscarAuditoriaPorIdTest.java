package com.tabloide.api.modules.auditoria.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.auditoria.domain.exceptions.RegistroAuditoriaNaoEncontradoException;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarAuditoriaPorIdTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private BuscarAuditoriaPorId buscarAuditoriaPorId;

    @BeforeEach
    void configurar() {
        buscarAuditoriaPorId = new BuscarAuditoriaPorId(auditoriaRepository);
    }

    private static RegistroAuditoria registroDoSupermercado(Long supermercadoId) {
        return new RegistroAuditoria(
                1L, 5L, Perfil.DONO, supermercadoId, "SUPERMERCADO_EDITADO", "Supermercado", supermercadoId,
                "antes", "depois", Instant.now()
        );
    }

    @Test
    void deveLancarNaoEncontradoQuandoRegistroNaoExiste() {
        when(auditoriaRepository.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarAuditoriaPorId.executar(999L, Perfil.SUPER_ADMIN, null))
                .isInstanceOf(RegistroAuditoriaNaoEncontradoException.class);
    }

    @Test
    void superAdminDeveConsultarRegistroDeQualquerSupermercado() {
        RegistroAuditoria registro = registroDoSupermercado(20L);
        when(auditoriaRepository.buscarPorId(1L)).thenReturn(Optional.of(registro));

        RegistroAuditoria resultado = buscarAuditoriaPorId.executar(1L, Perfil.SUPER_ADMIN, 10L);

        assertThat(resultado).isEqualTo(registro);
    }

    @Test
    void donoDeveConsultarRegistroDoProprioSupermercado() {
        RegistroAuditoria registro = registroDoSupermercado(10L);
        when(auditoriaRepository.buscarPorId(1L)).thenReturn(Optional.of(registro));

        RegistroAuditoria resultado = buscarAuditoriaPorId.executar(1L, Perfil.DONO, 10L);

        assertThat(resultado).isEqualTo(registro);
    }

    @Test
    void donoNaoDeveConsultarRegistroDeOutroSupermercado() {
        RegistroAuditoria registro = registroDoSupermercado(20L);
        when(auditoriaRepository.buscarPorId(1L)).thenReturn(Optional.of(registro));

        assertThatThrownBy(() -> buscarAuditoriaPorId.executar(1L, Perfil.DONO, 10L))
                .isInstanceOf(RegistroAuditoriaNaoEncontradoException.class);
    }
}
