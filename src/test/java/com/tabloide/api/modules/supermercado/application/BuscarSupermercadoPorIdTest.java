package com.tabloide.api.modules.supermercado.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarSupermercadoPorIdTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private SupermercadoRepository supermercadoRepository;

    private BuscarSupermercadoPorId buscarSupermercadoPorId;

    @BeforeEach
    void configurar() {
        buscarSupermercadoPorId = new BuscarSupermercadoPorId(supermercadoRepository);
    }

    @Test
    void deveLancarNaoEncontradoQuandoIdNaoExiste() {
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarSupermercadoPorId.executar(1L)).isInstanceOf(SupermercadoNaoEncontradoException.class);
    }

    @Test
    void deveRetornarSupermercadoExistente() {
        Instant agora = Instant.now();
        Supermercado supermercado = new Supermercado(1L, new Cnpj("11222333000181"), "Razão", "Fantasia", "e@e.com", "119999", ENDERECO, null, null, null, com.tabloide.api.modules.supermercado.domain.EstadoSupermercado.ATIVO, 0L, agora, agora);
        when(supermercadoRepository.buscarPorId(1L)).thenReturn(Optional.of(supermercado));

        assertThat(buscarSupermercadoPorId.executar(1L)).isEqualTo(supermercado);
    }
}
