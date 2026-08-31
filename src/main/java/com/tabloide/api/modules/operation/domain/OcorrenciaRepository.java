package com.tabloide.api.modules.operation.domain;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.List;
import java.util.Optional;

public interface OcorrenciaRepository {

    Optional<Ocorrencia> buscarPorId(Long id);

    Ocorrencia salvar(Ocorrencia ocorrencia);

    Pagina<Ocorrencia> listar(FiltroOcorrencia filtro, int pagina, int tamanho);

    void registrarEvento(EventoOcorrencia evento);

    List<EventoOcorrencia> listarEventos(Long ocorrenciaId);
}
