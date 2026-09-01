package com.tabloide.api.modules.qrcodes.application;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import com.tabloide.api.modules.qrcodes.domain.exceptions.QrCodeNaoEncontradoException;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class BuscarQrCodePorId {

    private final QrCodeRepository qrCodeRepository;

    public BuscarQrCodePorId(QrCodeRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    public QrCode executar(Long supermercadoId, Long id, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, perfilAtor, supermercadoIdAtor)) {
            throw new QrCodeNaoEncontradoException();
        }
        return qrCodeRepository.buscarPorIdESupermercado(id, supermercadoId).orElseThrow(QrCodeNaoEncontradoException::new);
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (perfilAtor == Perfil.SUPER_ADMIN) {
            return false;
        }
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
