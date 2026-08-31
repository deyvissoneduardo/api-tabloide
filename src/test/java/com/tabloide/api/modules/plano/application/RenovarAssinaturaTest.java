package com.tabloide.api.modules.plano.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoExcluidoException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import com.tabloide.api.modules.plano.domain.exceptions.TransicaoEstadoInvalidaException;
import com.tabloide.api.modules.supermercado.application.AtivarSupermercado;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RenovarAssinaturaTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private AssinaturaRepository assinaturaRepository;

    @Mock
    private AtivarSupermercado ativarSupermercado;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private RenovarAssinatura renovarAssinatura;

    @BeforeEach
    void configurar() {
        renovarAssinatura = new RenovarAssinatura(supermercadoRepository, planoRepository, assinaturaRepository, ativarSupermercado, auditoriaRepository);
    }

    private static Supermercado supermercado(EstadoSupermercado estado) {
        Instant agora = Instant.now();
        return new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, estado, 0L, agora, agora);
    }

    private static Plano plano(Long id, String nome) {
        Instant agora = Instant.now();
        return new Plano(id, nome, Plano.normalizarNome(nome), 30, BigDecimal.valueOf(99.90), 100, null, 0L, agora, agora, null);
    }

    private static Plano planoExcluido(Long id, String nome) {
        Instant agora = Instant.now();
        return new Plano(id, nome, Plano.normalizarNome(nome), 30, BigDecimal.valueOf(99.90), 100, null, 0L, agora, agora, agora);
    }

    @Test
    void deveLancarNaoEncontradoQuandoSupermercadoNaoExiste() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);
    }

    @Test
    void deveLancarAssinaturaNaoEncontradaQuandoNuncaHouveAssinatura() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarMaisRecentePorSupermercado(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(AssinaturaNaoEncontradaException.class);
    }

    @Test
    void deveLancarPlanoNaoEncontradoQuandoPlanoDaAssinaturaNaoExisteMais() {
        Assinatura vigente = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarMaisRecentePorSupermercado(1L)).thenReturn(Optional.of(vigente));
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(PlanoNaoEncontradoException.class);
    }

    @Test
    void deveLancarPlanoExcluidoQuandoPlanoAtualEstaExcluido() {
        Assinatura vigente = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarMaisRecentePorSupermercado(1L)).thenReturn(Optional.of(vigente));
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.of(planoExcluido(5L, "Básico")));

        assertThatThrownBy(() -> renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(PlanoExcluidoException.class);
    }

    @Test
    void deveRenovarAntecipadamenteQuandoAssinaturaVigenteESupermercadoAtivo() {
        Plano planoAtual = plano(5L, "Básico");
        Assinatura vigente = Assinatura.associar(1L, planoAtual, Instant.now());
        Instant dataFimOriginal = vigente.dataFim();
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarMaisRecentePorSupermercado(1L)).thenReturn(Optional.of(vigente));
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.of(planoAtual));
        when(assinaturaRepository.salvar(any(Assinatura.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Assinatura resultado = renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estado()).isEqualTo(EstadoAssinatura.VIGENTE);
        assertThat(resultado.dataFim()).isAfter(dataFimOriginal);
        verify(ativarSupermercado, never()).executar(any(), any(), any());
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveRejeitarRenovacaoAntecipadaQuandoSupermercadoNaoAtivo() {
        Assinatura vigente = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.BLOQUEADO)));
        when(assinaturaRepository.buscarMaisRecentePorSupermercado(1L)).thenReturn(Optional.of(vigente));
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.of(plano(5L, "Básico")));

        assertThatThrownBy(() -> renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void deveRenovarAposVencimentoQuandoAssinaturaVencidaESupermercadoBloqueado() {
        Assinatura vencida = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        vencida.vencer();
        Plano planoAtual = plano(5L, "Básico");
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.BLOQUEADO)));
        when(assinaturaRepository.buscarMaisRecentePorSupermercado(1L)).thenReturn(Optional.of(vencida));
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.of(planoAtual));
        when(assinaturaRepository.salvar(any(Assinatura.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Assinatura resultado = renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estado()).isEqualTo(EstadoAssinatura.VIGENTE);
        assertThat(resultado.planoId()).isEqualTo(5L);
        verify(ativarSupermercado).executar(1L, 1L, Perfil.SUPER_ADMIN);
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveRejeitarRenovacaoAposVencimentoQuandoSupermercadoNaoBloqueado() {
        Assinatura vencida = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        vencida.vencer();
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarMaisRecentePorSupermercado(1L)).thenReturn(Optional.of(vencida));
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.of(plano(5L, "Básico")));

        assertThatThrownBy(() -> renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void deveLancarTransicaoInvalidaQuandoAssinaturaNaoEstaVigenteNemVencida() {
        Assinatura substituida = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        substituida.substituir(Instant.now());
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarMaisRecentePorSupermercado(1L)).thenReturn(Optional.of(substituida));
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.of(plano(5L, "Básico")));

        assertThatThrownBy(() -> renovarAssinatura.executar(1L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(TransicaoEstadoInvalidaException.class);
    }
}
