package com.tabloide.api.modules.analytics.application;

import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

// US-011: identificar supermercados inativos ou com baixa utilização.
@Component
public class ListarSupermercadosComBaixaUtilizacao {

    // RN-013: baixa utilização = abaixo de 20% da mediana dos supermercados ativos no período; inativo = zero eventos.
    private static final BigDecimal PERCENTUAL_LIMIAR_BAIXA_UTILIZACAO = new BigDecimal("0.20");

    private final ContadorDeUtilizacaoPorPeriodo contador;
    private final SupermercadoRepository supermercadoRepository;

    public ListarSupermercadosComBaixaUtilizacao(ContadorDeUtilizacaoPorPeriodo contador, SupermercadoRepository supermercadoRepository) {
        this.contador = contador;
        this.supermercadoRepository = supermercadoRepository;
    }

    public ResultadoBaixaUtilizacao executar(Instant inicio, Instant fim) {
        Map<Long, Long> quantidades = contador.contar(inicio, fim);
        List<Supermercado> todos = supermercadoRepository.listarTodos();

        BigDecimal mediana = calcularMedianaDosAtivos(todos, quantidades);
        BigDecimal limiar = mediana.multiply(PERCENTUAL_LIMIAR_BAIXA_UTILIZACAO);

        List<SupermercadoBaixaUtilizacao> itens = todos.stream()
                .map(supermercado -> classificar(supermercado, quantidades.getOrDefault(supermercado.id(), 0L), limiar))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(SupermercadoBaixaUtilizacao::quantidadeEventos)
                        .thenComparing(SupermercadoBaixaUtilizacao::nomeFantasia))
                .toList();

        return new ResultadoBaixaUtilizacao(itens, mediana.doubleValue(), PERCENTUAL_LIMIAR_BAIXA_UTILIZACAO.doubleValue());
    }

    private SupermercadoBaixaUtilizacao classificar(Supermercado supermercado, long quantidade, BigDecimal limiar) {
        if (quantidade == 0) {
            return SupermercadoBaixaUtilizacao.de(supermercado, quantidade, ClassificacaoUtilizacao.INATIVO);
        }
        if (BigDecimal.valueOf(quantidade).compareTo(limiar) < 0) {
            return SupermercadoBaixaUtilizacao.de(supermercado, quantidade, ClassificacaoUtilizacao.BAIXA_UTILIZACAO);
        }
        return null;
    }

    private static BigDecimal calcularMedianaDosAtivos(List<Supermercado> todos, Map<Long, Long> quantidades) {
        List<Long> quantidadesDosAtivos = todos.stream()
                .filter(Supermercado::estaAtivo)
                .map(supermercado -> quantidades.getOrDefault(supermercado.id(), 0L))
                .sorted()
                .toList();
        return calcularMediana(quantidadesDosAtivos);
    }

    private static BigDecimal calcularMediana(List<Long> valoresOrdenados) {
        if (valoresOrdenados.isEmpty()) {
            return BigDecimal.ZERO;
        }
        int tamanho = valoresOrdenados.size();
        int meio = tamanho / 2;
        if (tamanho % 2 == 1) {
            return BigDecimal.valueOf(valoresOrdenados.get(meio));
        }
        return BigDecimal.valueOf(valoresOrdenados.get(meio - 1) + valoresOrdenados.get(meio))
                .divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);
    }
}
