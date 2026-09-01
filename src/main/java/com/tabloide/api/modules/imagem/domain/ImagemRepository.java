package com.tabloide.api.modules.imagem.domain;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.Optional;

public interface ImagemRepository {

    Optional<Imagem> buscarPorId(Long id);

    long contarAtivasPorSupermercado(Long supermercadoId);

    Imagem salvar(Imagem imagem);

    Pagina<Imagem> listarPorSupermercado(Long supermercadoId, String nomeBusca, int pagina, int tamanho);
}
