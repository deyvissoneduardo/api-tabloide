package com.tabloide.api.modules.qrcodes.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.analytics.domain.EventoAcessoRepository;
import com.tabloide.api.modules.analytics.domain.TipoEventoAcesso;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContarAcessosDeQrCodesTest {

    @Mock
    private EventoAcessoRepository eventoAcessoRepository;

    private ContarAcessosDeQrCodes contarAcessosDeQrCodes;

    @BeforeEach
    void configurar() {
        contarAcessosDeQrCodes = new ContarAcessosDeQrCodes(eventoAcessoRepository);
    }

    @Test
    void deveContarAcessosPorQrCodeNoPeriodoFiltrandoPorScanDeQrCode() {
        Instant inicio = Instant.now().minusSeconds(3600);
        Instant fim = Instant.now();
        when(eventoAcessoRepository.contarPorRecursoNoPeriodo(TipoEventoAcesso.SCAN_QR_CODE, List.of(1L, 2L), inicio, fim))
                .thenReturn(Map.of(1L, 5L));

        Map<Long, Long> resultado = contarAcessosDeQrCodes.executar(List.of(1L, 2L), inicio, fim);

        assertThat(resultado).containsEntry(1L, 5L);
        assertThat(resultado).doesNotContainKey(2L);
    }
}
