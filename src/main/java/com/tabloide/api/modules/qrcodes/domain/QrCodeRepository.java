package com.tabloide.api.modules.qrcodes.domain;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.Optional;

public interface QrCodeRepository {

    Optional<QrCode> buscarPorIdESupermercado(Long id, Long supermercadoId);

    boolean existeNomeNormalizadoNoSupermercado(String nomeNormalizado, Long supermercadoId);

    QrCode salvar(QrCode qrCode);

    Pagina<QrCode> listarPorLoja(Long lojaId, Long supermercadoId, int pagina, int tamanho);
}
