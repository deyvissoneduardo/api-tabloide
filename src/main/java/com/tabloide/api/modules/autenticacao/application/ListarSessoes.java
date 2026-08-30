package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada;
import com.tabloide.api.modules.autenticacao.domain.SessaoRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarSessoes {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final SessaoRepository sessaoRepository;

    public ListarSessoes(SessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public Pagina<SessaoDetalhada> executar(Perfil perfilSolicitante, Long supermercadoIdSolicitante, int pagina, int tamanho) {
        validarPaginacao(pagina, tamanho);
        return sessaoRepository.listar(escopoDoSolicitante(perfilSolicitante, supermercadoIdSolicitante), pagina, tamanho);
    }

    private Long escopoDoSolicitante(Perfil perfilSolicitante, Long supermercadoIdSolicitante) {
        return perfilSolicitante == Perfil.SUPER_ADMIN ? null : supermercadoIdSolicitante;
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
    }
}
