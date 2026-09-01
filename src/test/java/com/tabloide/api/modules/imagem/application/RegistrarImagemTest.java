package com.tabloide.api.modules.imagem.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.imagem.domain.FormatoImagem;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.ImagemRepository;
import com.tabloide.api.modules.imagem.domain.TipoVinculoImagem;
import com.tabloide.api.modules.imagem.domain.exceptions.CotaDeImagensExcedidaException;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarImagemTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private ImagemRepository imagemRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AssinaturaRepository assinaturaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private RegistrarImagem registrarImagem;

    @BeforeEach
    void configurar() {
        registrarImagem = new RegistrarImagem(imagemRepository, supermercadoRepository, assinaturaRepository, auditoriaRepository);
    }

    private static DadosImagem dadosValidos() {
        return new DadosImagem("Banner Black Friday", TipoVinculoImagem.BANNER, null, "s3://bucket/banner.jpg", FormatoImagem.JPG, 1024);
    }

    private static Supermercado supermercadoAtivo() {
        return new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null,
                EstadoSupermercado.ATIVO, 0L, Instant.now(), Instant.now());
    }

    private static Assinatura assinaturaComLimite(Integer limiteFotos) {
        return new Assinatura(1L, SUPERMERCADO_ID, 10L, "Plano", 30, BigDecimal.TEN, limiteFotos,
                EstadoAssinatura.VIGENTE, Instant.now(), Instant.now().plusSeconds(3600), Instant.now());
    }

    @Test
    void deveRejeitarQuandoSupermercadoBloqueadoOuDesativado() {
        Supermercado bloqueado = new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null,
                EstadoSupermercado.BLOQUEADO, 0L, Instant.now(), Instant.now());
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(bloqueado));

        assertThatThrownBy(() -> registrarImagem.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);

        verify(imagemRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoNaoHaAssinaturaVigente() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrarImagem.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO))
                .isInstanceOf(AssinaturaNaoEncontradaException.class);

        verify(imagemRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoCotaAtingida() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(SUPERMERCADO_ID))
                .thenReturn(Optional.of(assinaturaComLimite(2)));
        when(imagemRepository.contarAtivasPorSupermercado(SUPERMERCADO_ID)).thenReturn(2L);

        assertThatThrownBy(() -> registrarImagem.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO))
                .isInstanceOf(CotaDeImagensExcedidaException.class);

        verify(imagemRepository, never()).salvar(any());
    }

    @Test
    void devePermitirRegistroSemLimiteQuandoPlanoNaoDefineLimiteFotos() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(SUPERMERCADO_ID))
                .thenReturn(Optional.of(assinaturaComLimite(null)));
        when(imagemRepository.salvar(any(Imagem.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Imagem resultado = registrarImagem.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO);

        assertThat(resultado.estaAtiva()).isTrue();
        verify(imagemRepository, never()).contarAtivasPorSupermercado(any());
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveRegistrarERegistrarAuditoriaDentroDoLimite() {
        when(supermercadoRepository.buscarPorIdComLock(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(SUPERMERCADO_ID))
                .thenReturn(Optional.of(assinaturaComLimite(5)));
        when(imagemRepository.contarAtivasPorSupermercado(SUPERMERCADO_ID)).thenReturn(1L);
        when(imagemRepository.salvar(any(Imagem.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Imagem resultado = registrarImagem.executar(SUPERMERCADO_ID, dadosValidos(), 1L, Perfil.DONO);

        assertThat(resultado.nomeBusca()).isEqualTo("Banner Black Friday");
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
