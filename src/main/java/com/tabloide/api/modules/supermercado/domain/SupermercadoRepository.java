package com.tabloide.api.modules.supermercado.domain;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.Optional;

public interface SupermercadoRepository {

    Optional<Supermercado> buscarPorId(Long id);

    Optional<Supermercado> buscarPorIdComLock(Long id);

    boolean existePorCnpj(Cnpj cnpj);

    Supermercado salvar(Supermercado supermercado);

    Pagina<Supermercado> listar(int pagina, int tamanho);
}
