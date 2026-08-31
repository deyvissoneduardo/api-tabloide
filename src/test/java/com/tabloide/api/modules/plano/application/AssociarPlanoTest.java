package com.tabloide.api.modules.plano.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaVigenteJaExisteException;
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
class AssociarPlanoTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private AssinaturaRepository assinaturaRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private AssociarPlano associarPlano;

    @BeforeEach
    void configurar() {
        associarPlano = new AssociarPlano(supermercadoRepository, planoRepository, assinaturaRepository, auditoriaRepository);
    }

    private static Supermercado supermercado(EstadoSupermercado estado) {
        Instant agora = Instant.now();
        return new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, estado, 0L, agora, agora);
    }

    private static Plano plano() {
        return new Plano(5L, "Básico", 30, BigDecimal.valueOf(99.90), 100);
    }

    @Test
    void deveLancarNaoEncontradoQuandoSupermercadoNaoExiste() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> associarPlano.executar(1L, 5L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);
    }

    @Test
    void deveRejeitarQuandoSupermercadoBloqueado() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.BLOQUEADO)));

        assertThatThrownBy(() -> associarPlano.executar(1L, 5L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(SupermercadoBloqueadoOuDesativadoException.class);
    }

    @Test
    void deveRejeitarQuandoJaExisteAssinaturaVigente() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(1L))
                .thenReturn(Optional.of(Assinatura.associar(1L, plano(), Instant.now())));

        assertThatThrownBy(() -> associarPlano.executar(1L, 5L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(AssinaturaVigenteJaExisteException.class);
    }

    @Test
    void deveLancarPlanoNaoEncontradoQuandoPlanoNaoExiste() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(1L)).thenReturn(Optional.empty());
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> associarPlano.executar(1L, 5L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(PlanoNaoEncontradoException.class);
    }

    @Test
    void deveAssociarQuandoDadosValidos() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado(EstadoSupermercado.ATIVO)));
        when(assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(1L)).thenReturn(Optional.empty());
        when(planoRepository.buscarPorId(5L)).thenReturn(Optional.of(plano()));
        when(assinaturaRepository.salvar(any(Assinatura.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Assinatura resultado = associarPlano.executar(1L, 5L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estado()).isEqualTo(EstadoAssinatura.VIGENTE);
        assertThat(resultado.planoId()).isEqualTo(5L);
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
