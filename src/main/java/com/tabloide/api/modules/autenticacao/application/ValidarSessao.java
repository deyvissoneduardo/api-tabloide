package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.Sessao;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class ValidarSessao {

    private final SessaoRepository sessaoRepository;
    private final AutenticacaoProperties propriedades;

    public ValidarSessao(SessaoRepository sessaoRepository, AutenticacaoProperties propriedades) {
        this.sessaoRepository = sessaoRepository;
        this.propriedades = propriedades;
    }

    public void validar(String jti) {
        Instant agora = Instant.now();
        Sessao sessao = sessaoRepository.buscarPorJti(jti)
                .orElseThrow(SessaoInvalidaOuExpiradaException::new);

        if (!sessao.estaValida(agora, propriedades.sessao().tempoInatividade())) {
            throw new SessaoInvalidaOuExpiradaException();
        }

        sessao.registrarUso(agora);
        sessaoRepository.salvar(sessao);
    }
}
