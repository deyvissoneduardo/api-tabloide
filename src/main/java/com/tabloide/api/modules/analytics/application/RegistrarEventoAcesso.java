package com.tabloide.api.modules.analytics.application;

import com.tabloide.api.modules.analytics.domain.EventoAcesso;
import com.tabloide.api.modules.analytics.domain.EventoAcessoRepository;
import com.tabloide.api.modules.analytics.domain.TipoEventoAcesso;
import java.time.Instant;
import org.springframework.stereotype.Component;

// Usado por outros módulos (QR Codes, Publicação) para contabilizar acessos sem acoplar
// a leitura pública/redirecionamento às regras de agregação de analytics.
@Component
public class RegistrarEventoAcesso {

    private final EventoAcessoRepository eventoAcessoRepository;

    public RegistrarEventoAcesso(EventoAcessoRepository eventoAcessoRepository) {
        this.eventoAcessoRepository = eventoAcessoRepository;
    }

    public void executar(TipoEventoAcesso tipo, Long supermercadoId, Long lojaId, Long recursoId, String eventoTecnicoId) {
        eventoAcessoRepository.registrar(EventoAcesso.registrar(tipo, supermercadoId, lojaId, recursoId, eventoTecnicoId, Instant.now()));
    }
}
