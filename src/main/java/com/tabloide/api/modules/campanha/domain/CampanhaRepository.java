package com.tabloide.api.modules.campanha.domain;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CampanhaRepository {

    Optional<Campanha> buscarPorIdESupermercado(Long id, Long supermercadoId);

    List<Campanha> buscarPorIdsESupermercado(Set<Long> ids, Long supermercadoId);

    Campanha salvar(Campanha campanha);

    Pagina<Campanha> listarPorSupermercado(Long supermercadoId, int pagina, int tamanho);

    boolean existeOfertaEmCampanhaAtiva(Long supermercadoId, Long ofertaId, Long campanhaIdParaExcluir);
}
