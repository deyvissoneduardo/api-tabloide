package com.tabloide.api.modules.autenticacao.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Sessao;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidarSessaoTest {

    @Mock
    private SessaoRepository sessaoRepository;

    private ValidarSessao validarSessao;

    @BeforeEach
    void configurar() {
        AutenticacaoProperties propriedades = new AutenticacaoProperties(
                new AutenticacaoProperties.Sessao(Duration.ofHours(8), Duration.ofMinutes(30)),
                new AutenticacaoProperties.Login(5, Duration.ofMinutes(15)),
                new AutenticacaoProperties.RedefinicaoSenha(Duration.ofMinutes(15))
        );
        validarSessao = new ValidarSessao(sessaoRepository, propriedades);
    }

    @Test
    void deveLancarExcecaoQuandoSessaoNaoExiste() {
        when(sessaoRepository.buscarPorJti("jti-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validarSessao.validar("jti-inexistente"))
                .isInstanceOf(SessaoInvalidaOuExpiradaException.class);
    }

    @Test
    void deveLancarExcecaoQuandoSessaoRevogada() {
        Instant agora = Instant.now();
        Sessao sessao = Sessao.iniciar(1L, "jti-1", agora, Duration.ofHours(8));
        sessao.revogar(agora);
        when(sessaoRepository.buscarPorJti("jti-1")).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> validarSessao.validar("jti-1"))
                .isInstanceOf(SessaoInvalidaOuExpiradaException.class);
    }

    @Test
    void deveAtualizarUltimoUsoQuandoSessaoValida() {
        Instant agora = Instant.now();
        Sessao sessao = Sessao.iniciar(1L, "jti-1", agora, Duration.ofHours(8));
        when(sessaoRepository.buscarPorJti("jti-1")).thenReturn(Optional.of(sessao));

        validarSessao.validar("jti-1");

        verify(sessaoRepository).salvar(sessao);
    }
}
