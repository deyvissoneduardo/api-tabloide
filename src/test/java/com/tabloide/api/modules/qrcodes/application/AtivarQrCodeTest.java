package com.tabloide.api.modules.qrcodes.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
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
class AtivarQrCodeTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private QrCodeRepository qrCodeRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AtivarQrCode ativarQrCode;

    @BeforeEach
    void configurar() {
        ativarQrCode = new AtivarQrCode(qrCodeRepository, auditoriaRepository);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> ativarQrCode.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, 2L))
                .isInstanceOf(QrCodeNaoEncontradoException.class);

        verify(qrCodeRepository, never()).salvar(any());
    }

    @Test
    void deveAtivarQrCodeDesativado() {
        QrCode qrCode = new QrCode(1L, SUPERMERCADO_ID, 2L, "QR", "qr", "abc123", EstadoQrCode.DESATIVADO, 0L, Instant.now(), Instant.now());
        when(qrCodeRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(qrCode));
        when(qrCodeRepository.salvar(any(QrCode.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        QrCode resultado = ativarQrCode.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtivo()).isTrue();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveSerIdempotenteQuandoJaAtivo() {
        QrCode qrCode = new QrCode(1L, SUPERMERCADO_ID, 2L, "QR", "qr", "abc123", EstadoQrCode.ATIVO, 0L, Instant.now(), Instant.now());
        when(qrCodeRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(qrCode));

        QrCode resultado = ativarQrCode.executar(SUPERMERCADO_ID, 1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtivo()).isTrue();
        verify(qrCodeRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }
}
