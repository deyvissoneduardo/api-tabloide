package com.tabloide.api.modules.supermercado.domain;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import java.util.Optional;

public interface SupermercadoRepository {

    Optional<Supermercado> buscarPorId(Long id);

    boolean existePorCnpj(Cnpj cnpj);

    Supermercado salvar(Supermercado supermercado);
}
