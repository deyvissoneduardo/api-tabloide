package com.tabloide.api.modules.qrcodes.application;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarQrCodesPorLoja {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final QrCodeRepository qrCodeRepository;

    public ListarQrCodesPorLoja(QrCodeRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    public Pagina<QrCode> executar(Long supermercadoId, Long lojaId, Perfil perfilAtor, Long supermercadoIdAtor, int pagina, int tamanho) {
        if (foraDoEscopoDoAtor(supermercadoId, perfilAtor, supermercadoIdAtor)) {
            throw new LojaNaoEncontradaException();
        }
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
        return qrCodeRepository.listarPorLoja(lojaId, supermercadoId, pagina, tamanho);
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (perfilAtor == Perfil.SUPER_ADMIN) {
            return false;
        }
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
