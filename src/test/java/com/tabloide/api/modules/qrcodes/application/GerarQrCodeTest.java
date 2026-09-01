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
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import com.tabloide.api.modules.qrcodes.domain.exceptions.LojaIndisponivelParaQrCodeException;
import com.tabloide.api.modules.qrcodes.domain.exceptions.NomeDeQrCodeJaCadastradoException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GerarQrCodeTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long LOJA_ID = 2L;
    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private QrCodeRepository qrCodeRepository;

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private GerarQrCode gerarQrCode;

    @BeforeEach
    void configurar() {
        gerarQrCode = new GerarQrCode(qrCodeRepository, lojaRepository, auditoriaRepository);
    }

    private static DadosQrCode dadosValidos() {
        return new DadosQrCode("QR Entrada");
    }

    private static Loja lojaAtiva() {
        return new Loja(LOJA_ID, SUPERMERCADO_ID, "Loja Centro", "loja centro", ENDERECO, null, EstadoLoja.ATIVA, 0L, Instant.now(), Instant.now());
    }

    private static Loja lojaDesativada() {
        return new Loja(LOJA_ID, SUPERMERCADO_ID, "Loja Centro", "loja centro", ENDERECO, null, EstadoLoja.DESATIVADA, 0L, Instant.now(), Instant.now());
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> gerarQrCode.executar(SUPERMERCADO_ID, LOJA_ID, dadosValidos(), 1L, Perfil.DONO, 2L))
                .isInstanceOf(LojaNaoEncontradaException.class);

        verify(qrCodeRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoLojaNaoEncontrada() {
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gerarQrCode.executar(SUPERMERCADO_ID, LOJA_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(LojaNaoEncontradaException.class);

        verify(qrCodeRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoLojaDesativada() {
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaDesativada()));

        assertThatThrownBy(() -> gerarQrCode.executar(SUPERMERCADO_ID, LOJA_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(LojaIndisponivelParaQrCodeException.class);

        verify(qrCodeRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarNomeDuplicadoNoSupermercado() {
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaAtiva()));
        when(qrCodeRepository.existeNomeNormalizadoNoSupermercado("qr entrada", SUPERMERCADO_ID)).thenReturn(true);

        assertThatThrownBy(() -> gerarQrCode.executar(SUPERMERCADO_ID, LOJA_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(NomeDeQrCodeJaCadastradoException.class);

        verify(qrCodeRepository, never()).salvar(any());
    }

    @Test
    void deveGerarERegistrarAuditoria() {
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaAtiva()));
        when(qrCodeRepository.existeNomeNormalizadoNoSupermercado("qr entrada", SUPERMERCADO_ID)).thenReturn(false);
        when(qrCodeRepository.salvar(any(QrCode.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        QrCode resultado = gerarQrCode.executar(SUPERMERCADO_ID, LOJA_ID, dadosValidos(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtivo()).isTrue();
        assertThat(resultado.nome()).isEqualTo("QR Entrada");
        assertThat(resultado.codigoPublico()).isNotBlank();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
