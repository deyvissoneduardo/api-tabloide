package com.tabloide.api.modules.qrcodes.application;

import com.tabloide.api.modules.analytics.application.RegistrarEventoAcesso;
import com.tabloide.api.modules.analytics.domain.TipoEventoAcesso;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import com.tabloide.api.modules.qrcodes.domain.exceptions.QrCodeIndisponivelException;
import org.springframework.stereotype.Component;

// US-106/US-170/US-188: resolve o destino público de um QR Code sem depender da campanha vigente
// e contabiliza o acesso (RN-008/RN-009 de QR Codes).
@Component
public class AcessarQrCode {

    private final QrCodeRepository qrCodeRepository;
    private final LojaRepository lojaRepository;
    private final RegistrarEventoAcesso registrarEventoAcesso;

    public AcessarQrCode(QrCodeRepository qrCodeRepository, LojaRepository lojaRepository, RegistrarEventoAcesso registrarEventoAcesso) {
        this.qrCodeRepository = qrCodeRepository;
        this.lojaRepository = lojaRepository;
        this.registrarEventoAcesso = registrarEventoAcesso;
    }

    public DestinoQrCode executar(String codigoPublico, String eventoTecnicoId) {
        QrCode qrCode = qrCodeRepository.buscarPorCodigoPublico(codigoPublico)
                .filter(QrCode::estaAtivo)
                .orElseThrow(QrCodeIndisponivelException::new);
        Loja loja = lojaRepository.buscarPorId(qrCode.lojaId())
                .filter(Loja::estaAtiva)
                .orElseThrow(QrCodeIndisponivelException::new);

        registrarEventoAcesso.executar(TipoEventoAcesso.SCAN_QR_CODE, qrCode.supermercadoId(), loja.id(), qrCode.id(), eventoTecnicoId);

        return new DestinoQrCode(qrCode.supermercadoId(), loja.id());
    }
}
