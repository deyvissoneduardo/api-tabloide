package com.tabloide.api.modules.analytics.application;

import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

// US-010: identificar supermercados com maior utilização da plataforma.
@Component
public class ListarSupermercadosPorUtilizacao {

    private final ContadorDeUtilizacaoPorPeriodo contador;
    private final SupermercadoRepository supermercadoRepository;

    public ListarSupermercadosPorUtilizacao(ContadorDeUtilizacaoPorPeriodo contador, SupermercadoRepository supermercadoRepository) {
        this.contador = contador;
        this.supermercadoRepository = supermercadoRepository;
    }

    public List<UtilizacaoSupermercado> executar(Instant inicio, Instant fim) {
        Map<Long, Long> quantidadesAtuais = contador.contar(inicio, fim);
        Instant[] periodoAnterior = contador.periodoAnteriorEquivalente(inicio, fim);
        Map<Long, Long> quantidadesAnteriores = contador.contar(periodoAnterior[0], periodoAnterior[1]);

        return supermercadoRepository.listarTodos().stream()
                .map(supermercado -> UtilizacaoSupermercado.calcular(
                        supermercado,
                        quantidadesAtuais.getOrDefault(supermercado.id(), 0L),
                        quantidadesAnteriores.getOrDefault(supermercado.id(), 0L)
                ))
                // RN-009: rankings ordenam por contagem decrescente, depois por nome e identificador.
                .sorted(Comparator.comparingLong(UtilizacaoSupermercado::quantidadeEventosPeriodoAtual).reversed()
                        .thenComparing(UtilizacaoSupermercado::nomeFantasia)
                        .thenComparing(UtilizacaoSupermercado::supermercadoId))
                .toList();
    }
}
