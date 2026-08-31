package com.tabloide.api.modules.plano.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
import com.tabloide.api.modules.plano.domain.exceptions.NenhumaAssinaturaVigenteException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
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
class AlterarPlanoSupermercadoTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private AssinaturaRepository assinaturaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AlterarPlanoSupermercado alterarPlanoSupermercado;

    @BeforeEach
    void configurar() {
        alterarPlanoSupermercado = new AlterarPlanoSupermercado(supermercadoRepository, planoRepository, assinaturaRepository, auditoriaRepository);
    }

    private static Supermercado supermercado(EstadoSupermercado estado) {
        Instant agora = Instant.now();
        return new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, estado, 0L, agora, agora);
    }

    private static Plano plano(Long id, String nome) {
        return new Plano(id, nome, 30, BigDecimal.valueOf(99.90), 100);
    }

    @Test
    void deveLancarNaoEncontradoQuandoSupermercadoNaoExiste() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alterarPlanoSupermercado.executar(1L, 6L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);
    }

    @Test
    void deveRejeitarQuandoSupermercadoBloqueado() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.BLOQUEADO)));

        assertThatThrownBy(() -> alterarPlanoSupermercado.executar(1L, 6L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void deveRejeitarQuandoNaoHaAssinaturaVigente() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alterarPlanoSupermercado.executar(1L, 6L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(NenhumaAssinaturaVigenteException.class);
    }

    @Test
    void deveLancarPlanoNaoEncontradoQuandoNovoPlanoNaoExiste() {
        Assinatura vigente = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(1L)).thenReturn(Optional.of(vigente));
        when(planoRepository.buscarPorId(6L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alterarPlanoSupermercado.executar(1L, 6L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(PlanoNaoEncontradoException.class);
    }

    @Test
    void deveTrocarPlanoESubstituirAssinaturaAnterior() {
        Assinatura vigente = Assinatura.associar(1L, plano(5L, "Básico"), Instant.now());
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(1L)).thenReturn(Optional.of(vigente));
        when(planoRepository.buscarPorId(6L)).thenReturn(Optional.of(plano(6L, "Premium")));
        when(assinaturaRepository.salvar(any(Assinatura.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Assinatura resultado = alterarPlanoSupermercado.executar(1L, 6L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.planoId()).isEqualTo(6L);
        assertThat(resultado.estado()).isEqualTo(EstadoAssinatura.VIGENTE);
        assertThat(vigente.estado()).isEqualTo(EstadoAssinatura.SUBSTITUIDA);
        verify(assinaturaRepository, times(2)).salvar(any(Assinatura.class));
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
