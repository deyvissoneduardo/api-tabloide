package com.tabloide.api.modules.qrcodes.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.qrcodes.domain.EstadoQrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import com.tabloide.api.modules.qrcodes.domain.exceptions.QrCodeNaoEncontradoException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarQrCodePorIdTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private QrCodeRepository qrCodeRepository;

    private BuscarQrCodePorId buscarQrCodePorId;

    @BeforeEach
    void configurar() {
        buscarQrCodePorId = new BuscarQrCodePorId(qrCodeRepository);
    }

    @Test
    void deveRejeitarDonoForaDoEscopo() {
        assertThatThrownBy(() -> buscarQrCodePorId.executar(SUPERMERCADO_ID, 1L, Perfil.DONO, 2L))
                .isInstanceOf(QrCodeNaoEncontradoException.class);
    }

    @Test
    void deveRejeitarQuandoNaoEncontrada() {
        when(qrCodeRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarQrCodePorId.executar(SUPERMERCADO_ID, 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(QrCodeNaoEncontradoException.class);
    }

    @Test
    void deveRetornarQrCodeEncontrado() {
        QrCode qrCode = new QrCode(1L, SUPERMERCADO_ID, 2L, "QR", "qr", "abc123", EstadoQrCode.ATIVO, 0L, Instant.now(), Instant.now());
        when(qrCodeRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(qrCode));

        QrCode resultado = buscarQrCodePorId.executar(SUPERMERCADO_ID, 1L, Perfil.SUPER_ADMIN, 999L);

        assertThat(resultado.id()).isEqualTo(1L);
    }
}
