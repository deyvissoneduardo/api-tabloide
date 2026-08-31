package com.tabloide.api.modules.plano.domain;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.Optional;

public interface PlanoRepository {

    Optional<Plano> buscarPorId(Long id);

    Plano salvar(Plano plano);

    boolean existeComNomeNormalizado(String nomeNormalizado, Long idParaExcluir);

    Pagina<Plano> listar(int pagina, int tamanho);
}
