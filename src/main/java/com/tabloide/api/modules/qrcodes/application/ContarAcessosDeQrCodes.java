package com.tabloide.api.modules.qrcodes.application;

import com.tabloide.api.modules.analytics.domain.EventoAcessoRepository;
import com.tabloide.api.modules.analytics.domain.TipoEventoAcesso;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import org.springframework.stereotype.Component;

// US-107: identificar qual QR Code originou os acessos, contando os scans (RN-008) por QR Code no período.
@Component
public class ContarAcessosDeQrCodes {

    private final EventoAcessoRepository eventoAcessoRepository;

    public ContarAcessosDeQrCodes(EventoAcessoRepository eventoAcessoRepository) {
        this.eventoAcessoRepository = eventoAcessoRepository;
    }

    public Map<Long, Long> executar(Collection<Long> qrCodeIds, Instant inicio, Instant fim) {
        return eventoAcessoRepository.contarPorRecursoNoPeriodo(TipoEventoAcesso.SCAN_QR_CODE, qrCodeIds, inicio, fim);
    }
}
