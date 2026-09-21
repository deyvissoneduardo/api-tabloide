package com.tabloide.api.modules.analytics.interfaces.http;

import com.tabloide.api.modules.analytics.interfaces.http.dto.ResultadoBaixaUtilizacaoResponse;
import com.tabloide.api.modules.analytics.interfaces.http.dto.UtilizacaoSupermercadoResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

// US-222/RN-014: converte os relatórios de métricas já calculados para CSV. É só apresentação —
// os filtros e o escopo são os mesmos já aplicados pelos casos de uso de leitura.
final class RelatorioCsvWriter {

    private static final String QUEBRA_LINHA = "\r\n";

    private RelatorioCsvWriter() {
    }

    static byte[] paraUtilizacao(List<UtilizacaoSupermercadoResponse> itens) {
        StringBuilder csv = new StringBuilder("supermercadoId,nomeFantasia,quantidadeEventosPeriodoAtual,quantidadeEventosPeriodoAnterior,variacaoPercentual")
                .append(QUEBRA_LINHA);
        for (UtilizacaoSupermercadoResponse item : itens) {
            csv.append(item.supermercadoId()).append(',')
                    .append(escapar(item.nomeFantasia())).append(',')
                    .append(item.quantidadeEventosPeriodoAtual()).append(',')
                    .append(item.quantidadeEventosPeriodoAnterior()).append(',')
                    .append(item.variacaoPercentual() != null ? item.variacaoPercentual() : "")
                    .append(QUEBRA_LINHA);
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    static byte[] paraBaixaUtilizacao(ResultadoBaixaUtilizacaoResponse resultado) {
        StringBuilder csv = new StringBuilder("supermercadoId,nomeFantasia,quantidadeEventos,classificacao").append(QUEBRA_LINHA);
        for (ResultadoBaixaUtilizacaoResponse.ItemResponse item : resultado.itens()) {
            csv.append(item.supermercadoId()).append(',')
                    .append(escapar(item.nomeFantasia())).append(',')
                    .append(item.quantidadeEventos()).append(',')
                    .append(item.classificacao())
                    .append(QUEBRA_LINHA);
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        if (!precisaDeAspas(valor)) {
            return valor;
        }
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }

    private static boolean precisaDeAspas(String valor) {
        return valor.contains(",") || valor.contains("\"") || valor.contains("\n") || valor.contains("\r");
    }
}
