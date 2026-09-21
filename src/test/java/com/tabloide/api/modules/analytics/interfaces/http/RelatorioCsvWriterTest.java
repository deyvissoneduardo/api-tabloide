package com.tabloide.api.modules.analytics.interfaces.http;

import static org.assertj.core.api.Assertions.assertThat;

import com.tabloide.api.modules.analytics.application.ClassificacaoUtilizacao;
import com.tabloide.api.modules.analytics.interfaces.http.dto.ResultadoBaixaUtilizacaoResponse;
import com.tabloide.api.modules.analytics.interfaces.http.dto.ResultadoBaixaUtilizacaoResponse.ItemResponse;
import com.tabloide.api.modules.analytics.interfaces.http.dto.UtilizacaoSupermercadoResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class RelatorioCsvWriterTest {

    @Test
    void deveGerarCsvDeUtilizacaoComCabecalhoEValoresFormatados() {
        List<UtilizacaoSupermercadoResponse> itens = List.of(
                new UtilizacaoSupermercadoResponse(1L, "Mercado, Bom Preço", 10L, 5L, 100.0),
                new UtilizacaoSupermercadoResponse(2L, "Mercado Sem Base", 3L, 0L, null)
        );

        String csv = new String(RelatorioCsvWriter.paraUtilizacao(itens), StandardCharsets.UTF_8);
        List<String> linhas = List.of(csv.split("\r\n"));

        assertThat(linhas.get(0)).isEqualTo(
                "supermercadoId,nomeFantasia,quantidadeEventosPeriodoAtual,quantidadeEventosPeriodoAnterior,variacaoPercentual");
        assertThat(linhas.get(1)).isEqualTo("1,\"Mercado, Bom Preço\",10,5,100.0");
        assertThat(linhas.get(2)).isEqualTo("2,Mercado Sem Base,3,0,");
    }

    @Test
    void deveGerarCsvDeBaixaUtilizacao() {
        ResultadoBaixaUtilizacaoResponse resultado = new ResultadoBaixaUtilizacaoResponse(
                List.of(new ItemResponse(1L, "Mercado Inativo", 0L, ClassificacaoUtilizacao.INATIVO.name())),
                50.0, 20.0
        );

        String csv = new String(RelatorioCsvWriter.paraBaixaUtilizacao(resultado), StandardCharsets.UTF_8);
        List<String> linhas = List.of(csv.split("\r\n"));

        assertThat(linhas.get(0)).isEqualTo("supermercadoId,nomeFantasia,quantidadeEventos,classificacao");
        assertThat(linhas.get(1)).isEqualTo("1,Mercado Inativo,0,INATIVO");
    }
}
