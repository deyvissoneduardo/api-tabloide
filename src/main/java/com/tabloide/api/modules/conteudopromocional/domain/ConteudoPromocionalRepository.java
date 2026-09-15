package com.tabloide.api.modules.conteudopromocional.domain;

import java.util.List;
import java.util.Optional;

public interface ConteudoPromocionalRepository {

    ConteudoPromocional salvar(ConteudoPromocional conteudoPromocional);

    Optional<ConteudoPromocional> buscarPorIdESupermercado(Long id, Long supermercadoId);

    List<ConteudoPromocional> listarPorSupermercadoOrdenados(Long supermercadoId);

    long contarPorSupermercado(Long supermercadoId);
}
