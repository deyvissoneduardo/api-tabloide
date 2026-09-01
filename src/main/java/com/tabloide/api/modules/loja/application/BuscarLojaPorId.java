package com.tabloide.api.modules.loja.application;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class BuscarLojaPorId {

    private final LojaRepository lojaRepository;

    public BuscarLojaPorId(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    public Loja executar(Long supermercadoId, Long id, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, perfilAtor, supermercadoIdAtor)) {
            throw new LojaNaoEncontradaException();
        }
        return lojaRepository.buscarPorIdESupermercado(id, supermercadoId).orElseThrow(LojaNaoEncontradaException::new);
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (perfilAtor == Perfil.SUPER_ADMIN) {
            return false;
        }
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
