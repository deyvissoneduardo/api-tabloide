package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarUsuariosPorSupermercado {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final UsuarioRepository usuarioRepository;

    public ListarUsuariosPorSupermercado(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Pagina<Usuario> executar(Long supermercadoId, int pagina, int tamanho) {
        validarPaginacao(pagina, tamanho);
        return usuarioRepository.listarPorSupermercado(supermercadoId, pagina, tamanho);
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
    }
}
