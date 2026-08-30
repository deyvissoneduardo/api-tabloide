package com.tabloide.api.modules.autenticacao.application;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AutenticacaoProperties(Sessao sessao, Login login, RedefinicaoSenha redefinicaoSenha) {

    public record Sessao(Duration duracaoMaxima, Duration tempoInatividade) {
    }

    public record Login(int maxTentativas, Duration bloqueioTemporario) {
    }

    public record RedefinicaoSenha(Duration validade) {
    }
}
