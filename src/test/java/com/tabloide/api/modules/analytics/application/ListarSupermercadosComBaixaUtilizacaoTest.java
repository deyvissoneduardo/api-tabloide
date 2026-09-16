package com.tabloide.api.modules.analytics.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarSupermercadosComBaixaUtilizacaoTest {

    private static final Instant INICIO = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant FIM = Instant.parse("2026-01-31T23:59:59Z");

    @Mock
    private ContadorDeUtilizacaoPorPeriodo contador;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    private ListarSupermercadosComBaixaUtilizacao listar;

    @BeforeEach
    void configurar() {
        listar = new ListarSupermercadosComBaixaUtilizacao(contador, supermercadoRepository);
    }

    private static Supermercado supermercadoAtivo(Long id, String nome) {
        Instant agora = Instant.now();
        return new Supermercado(id, null, "Razão", nome, "e@e.com", "119999", null, null, null, null,
                EstadoSupermercado.ATIVO, 0L, agora, agora);
    }

    // RN-013: baixa utilização = abaixo de 20% da mediana dos supermercados ativos; inativo = zero eventos.
    @Test
    void deveClassificarPorMedianaDosAtivos() {
        Supermercado inativo = supermercadoAtivo(1L, "Inativo");
        Supermercado baixaUtilizacao = supermercadoAtivo(2L, "Baixa utilização");
        Supermercado mediano = supermercadoAtivo(3L, "Mediano");
        Supermercado altaUtilizacao = supermercadoAtivo(4L, "Alta utilização");
        when(supermercadoRepository.listarTodos()).thenReturn(List.of(inativo, baixaUtilizacao, mediano, altaUtilizacao));
        // mediana de [0, 1, 100, 100] = (1 + 100) / 2 = 50,5 -> limiar de baixa utilização = 10,1
        when(contador.contar(INICIO, FIM)).thenReturn(Map.of(2L, 1L, 3L, 100L, 4L, 100L));

        ResultadoBaixaUtilizacao resultado = listar.executar(INICIO, FIM);

        assertThat(resultado.medianaSupermercadosAtivos()).isEqualTo(50.5);
        assertThat(resultado.itens()).hasSize(2);
        assertThat(resultado.itens()).anySatisfy(item -> {
            assertThat(item.supermercadoId()).isEqualTo(1L);
            assertThat(item.classificacao()).isEqualTo(ClassificacaoUtilizacao.INATIVO);
        });
        assertThat(resultado.itens()).anySatisfy(item -> {
            assertThat(item.supermercadoId()).isEqualTo(2L);
            assertThat(item.classificacao()).isEqualTo(ClassificacaoUtilizacao.BAIXA_UTILIZACAO);
        });
        assertThat(resultado.itens()).noneMatch(item -> item.supermercadoId().equals(3L) || item.supermercadoId().equals(4L));
    }

    @Test
    void naoDeveClassificarNenhumQuandoTodosOsAtivosTiveremUtilizacaoSemelhante() {
        Supermercado supermercado = supermercadoAtivo(1L, "Único ativo");
        when(supermercadoRepository.listarTodos()).thenReturn(List.of(supermercado));
        when(contador.contar(INICIO, FIM)).thenReturn(Map.of(1L, 10L));

        ResultadoBaixaUtilizacao resultado = listar.executar(INICIO, FIM);

        assertThat(resultado.itens()).isEmpty();
    }
}
