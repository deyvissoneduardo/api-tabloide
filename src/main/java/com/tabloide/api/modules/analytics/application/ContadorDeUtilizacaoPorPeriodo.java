package com.tabloide.api.modules.analytics.application;

import com.tabloide.api.modules.analytics.domain.ContagemPorSupermercado;
import com.tabloide.api.modules.analytics.domain.EventoAcessoRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

// Colaborador interno de ListarSupermercadosPorUtilizacao e ListarSupermercadosComBaixaUtilizacao:
// conta eventos por supermercado num período e resolve o período anterior equivalente para comparação (RN-010).
@Component
class ContadorDeUtilizacaoPorPeriodo {

    private final EventoAcessoRepository eventoAcessoRepository;

    ContadorDeUtilizacaoPorPeriodo(EventoAcessoRepository eventoAcessoRepository) {
        this.eventoAcessoRepository = eventoAcessoRepository;
    }

    Map<Long, Long> contar(Instant inicio, Instant fim) {
        return eventoAcessoRepository.contarPorSupermercadoNoPeriodo(inicio, fim).stream()
                .collect(Collectors.toMap(ContagemPorSupermercado::supermercadoId, ContagemPorSupermercado::quantidade));
    }

    Instant[] periodoAnteriorEquivalente(Instant inicio, Instant fim) {
        Duration duracao = Duration.between(inicio, fim);
        Instant fimAnterior = inicio.minusNanos(1);
        Instant inicioAnterior = fimAnterior.minus(duracao);
        return new Instant[]{inicioAnterior, fimAnterior};
    }
}
