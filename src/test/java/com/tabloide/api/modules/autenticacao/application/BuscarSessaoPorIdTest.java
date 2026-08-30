package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoNaoEncontradaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarSessaoPorIdTest {

    @Mock
    private SessaoRepository sessaoRepository;

    private BuscarSessaoPorId buscarSessaoPorId;

    @BeforeEach
    void configurar() {
        buscarSessaoPorId = new BuscarSessaoPorId(sessaoRepository);
    }

    private static SessaoDetalhada sessaoDoSupermercado(Long supermercadoId) {
        Instant agora = Instant.now();
        return new SessaoDetalhada("jti-1", "dono@sgtm.local", Perfil.DONO, supermercadoId, agora, agora.plusSeconds(3600), agora, null);
    }

    @Test
    void deveLancarNaoEncontradaQuandoSessaoNaoExiste() {
        when(sessaoRepository.buscarDetalhePorJti("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarSessaoPorId.executar("inexistente", Perfil.SUPER_ADMIN, null))
                .isInstanceOf(SessaoNaoEncontradaException.class);
    }

    @Test
    void superAdminDeveConsultarSessaoDeQualquerSupermercado() {
        SessaoDetalhada sessao = sessaoDoSupermercado(20L);
        when(sessaoRepository.buscarDetalhePorJti("jti-1")).thenReturn(Optional.of(sessao));

        SessaoDetalhada resultado = buscarSessaoPorId.executar("jti-1", Perfil.SUPER_ADMIN, 10L);

        assertThat(resultado).isEqualTo(sessao);
    }

    @Test
    void donoDeveConsultarSessaoDoProprioSupermercado() {
        SessaoDetalhada sessao = sessaoDoSupermercado(10L);
        when(sessaoRepository.buscarDetalhePorJti("jti-1")).thenReturn(Optional.of(sessao));

        SessaoDetalhada resultado = buscarSessaoPorId.executar("jti-1", Perfil.DONO, 10L);

        assertThat(resultado).isEqualTo(sessao);
    }

    @Test
    void donoNaoDeveConsultarSessaoDeOutroSupermercado() {
        SessaoDetalhada sessao = sessaoDoSupermercado(20L);
        when(sessaoRepository.buscarDetalhePorJti("jti-1")).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> buscarSessaoPorId.executar("jti-1", Perfil.DONO, 10L))
                .isInstanceOf(SessaoNaoEncontradaException.class);
    }
}
