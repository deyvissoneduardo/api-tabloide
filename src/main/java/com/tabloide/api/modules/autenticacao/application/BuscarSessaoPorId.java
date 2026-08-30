package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoNaoEncontradaException;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class BuscarSessaoPorId {

    private final SessaoRepository sessaoRepository;

    public BuscarSessaoPorId(SessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public SessaoDetalhada executar(String jti, Perfil perfilSolicitante, Long supermercadoIdSolicitante) {
        SessaoDetalhada sessao = sessaoRepository.buscarDetalhePorJti(jti)
                .orElseThrow(SessaoNaoEncontradaException::new);

        if (foraDoEscopo(sessao, perfilSolicitante, supermercadoIdSolicitante)) {
            throw new SessaoNaoEncontradaException();
        }

        return sessao;
    }

    private boolean foraDoEscopo(SessaoDetalhada sessao, Perfil perfilSolicitante, Long supermercadoIdSolicitante) {
        if (perfilSolicitante == Perfil.SUPER_ADMIN) {
            return false;
        }
        return !Objects.equals(sessao.supermercadoId(), supermercadoIdSolicitante);
    }
}
