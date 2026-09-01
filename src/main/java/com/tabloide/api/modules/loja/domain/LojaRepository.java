package com.tabloide.api.modules.loja.domain;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.Optional;

public interface LojaRepository {

    Optional<Loja> buscarPorIdESupermercado(Long id, Long supermercadoId);

    boolean existeNomeNormalizadoNoSupermercado(String nomeNormalizado, Long supermercadoId);

    long contarAtivasPorSupermercado(Long supermercadoId);

    Loja salvar(Loja loja);

    Pagina<Loja> listarPorSupermercado(Long supermercadoId, int pagina, int tamanho);
}
