package com.tabloide.api.modules.autenticacao.domain;

import java.util.Optional;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorEmailECnpj(String email, Cnpj cnpj);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorIdESupermercado(Long id, Long supermercadoId);

    Usuario salvar(Usuario usuario);

    Pagina<Usuario> listarPorSupermercado(Long supermercadoId, int pagina, int tamanho);
}
