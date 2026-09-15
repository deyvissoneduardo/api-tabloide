package com.tabloide.api.modules.conteudogeral.domain;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.Optional;

public interface ConteudoGeralRepository {

    Optional<ConteudoGeral> buscarPorId(Long id);

    Optional<ConteudoGeral> buscarPublicadoPorTipo(TipoConteudoGeral tipo);

    ConteudoGeral salvar(ConteudoGeral conteudoGeral);

    Pagina<ConteudoGeral> listar(TipoConteudoGeral tipo, int pagina, int tamanho);
}
