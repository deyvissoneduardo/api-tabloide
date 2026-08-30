package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class EncerrarSessao {

    private final SessaoRepository sessaoRepository;

    public EncerrarSessao(SessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public void encerrar(String jti) {
        sessaoRepository.buscarPorJti(jti).ifPresent(sessao -> {
            sessao.revogar(Instant.now());
            sessaoRepository.salvar(sessao);
        });
    }
}
