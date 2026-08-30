package com.tabloide.api.modules.autenticacao.domain;

import java.time.Instant;
import java.util.Optional;

public interface SessaoRepository {

    void salvar(Sessao sessao);

    Optional<Sessao> buscarPorJti(String jti);

    void revogarTodasDoUsuario(Long usuarioId, Instant agora);

    Pagina<SessaoDetalhada> listar(Long supermercadoId, int pagina, int tamanho);

    Optional<SessaoDetalhada> buscarDetalhePorJti(String jti);
}
