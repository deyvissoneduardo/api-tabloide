package com.tabloide.api.modules.loja.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarLojaPorIdTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private LojaRepository lojaRepository;

    private BuscarLojaPorId buscarLojaPorId;

    @BeforeEach
    void configurar() {
        buscarLojaPorId = new BuscarLojaPorId(lojaRepository);
    }

    @Test
    void deveRejeitarDonoForaDoEscopo() {
        assertThatThrownBy(() -> buscarLojaPorId.executar(SUPERMERCADO_ID, 1L, Perfil.DONO, 2L))
                .isInstanceOf(LojaNaoEncontradaException.class);
    }

    @Test
    void deveRejeitarQuandoNaoEncontrada() {
        when(lojaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarLojaPorId.executar(SUPERMERCADO_ID, 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(LojaNaoEncontradaException.class);
    }

    @Test
    void deveRetornarLojaEncontrada() {
        Loja loja = new Loja(1L, SUPERMERCADO_ID, "Loja", "loja", ENDERECO, null, EstadoLoja.ATIVA, 0L, Instant.now(), Instant.now());
        when(lojaRepository.buscarPorIdESupermercado(1L, SUPERMERCADO_ID)).thenReturn(Optional.of(loja));

        Loja resultado = buscarLojaPorId.executar(SUPERMERCADO_ID, 1L, Perfil.SUPER_ADMIN, 999L);

        assertThat(resultado.id()).isEqualTo(1L);
    }
}
