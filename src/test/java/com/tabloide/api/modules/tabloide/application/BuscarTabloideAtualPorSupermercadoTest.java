package com.tabloide.api.modules.tabloide.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import com.tabloide.api.modules.tabloide.domain.Tabloide;
import com.tabloide.api.modules.tabloide.domain.TabloideRepository;
import com.tabloide.api.modules.tabloide.domain.TipoArquivoTabloide;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarTabloideAtualPorSupermercadoTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private TabloideRepository tabloideRepository;

    private BuscarTabloideAtualPorSupermercado buscarTabloideAtualPorSupermercado;

    @BeforeEach
    void configurar() {
        buscarTabloideAtualPorSupermercado = new BuscarTabloideAtualPorSupermercado(tabloideRepository);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> buscarTabloideAtualPorSupermercado.executar(SUPERMERCADO_ID, 2L))
                .isInstanceOf(SupermercadoNaoEncontradoException.class);
    }

    @Test
    void deveRetornarVazioQuandoNaoHaTabloideVigente() {
        when(tabloideRepository.buscarVigentePorSupermercado(eq(SUPERMERCADO_ID), any(Instant.class))).thenReturn(Optional.empty());

        Optional<Tabloide> resultado = buscarTabloideAtualPorSupermercado.executar(SUPERMERCADO_ID, SUPERMERCADO_ID);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveRetornarOTabloideVigente() {
        Instant agora = Instant.now();
        Tabloide vigente = Tabloide.disponibilizar(
                SUPERMERCADO_ID, "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivo/tabloide.pdf", 1024L,
                Set.of(20L), agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), agora);
        when(tabloideRepository.buscarVigentePorSupermercado(eq(SUPERMERCADO_ID), any(Instant.class))).thenReturn(Optional.of(vigente));

        Optional<Tabloide> resultado = buscarTabloideAtualPorSupermercado.executar(SUPERMERCADO_ID, SUPERMERCADO_ID);

        assertThat(resultado).contains(vigente);
    }
}
