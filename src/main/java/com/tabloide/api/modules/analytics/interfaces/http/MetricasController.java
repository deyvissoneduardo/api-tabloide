package com.tabloide.api.modules.analytics.interfaces.http;

import com.tabloide.api.modules.analytics.application.ListarSupermercadosComBaixaUtilizacao;
import com.tabloide.api.modules.analytics.application.ListarSupermercadosPorUtilizacao;
import com.tabloide.api.modules.analytics.interfaces.http.dto.ResultadoBaixaUtilizacaoResponse;
import com.tabloide.api.modules.analytics.interfaces.http.dto.UtilizacaoSupermercadoResponse;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/metricas/supermercados")
@Tag(name = "Metricas")
public class MetricasController {

    // RN-007 (Analytics): período padrão de consulta é de 30 dias quando não informado.
    private static final int DIAS_PERIODO_PADRAO = 30;
    private static final MediaType TEXT_CSV = MediaType.valueOf("text/csv; charset=UTF-8");

    private final ListarSupermercadosPorUtilizacao listarSupermercadosPorUtilizacao;
    private final ListarSupermercadosComBaixaUtilizacao listarSupermercadosComBaixaUtilizacao;

    public MetricasController(
            ListarSupermercadosPorUtilizacao listarSupermercadosPorUtilizacao,
            ListarSupermercadosComBaixaUtilizacao listarSupermercadosComBaixaUtilizacao
    ) {
        this.listarSupermercadosPorUtilizacao = listarSupermercadosPorUtilizacao;
        this.listarSupermercadosComBaixaUtilizacao = listarSupermercadosComBaixaUtilizacao;
    }

    @GetMapping(path = "/mais-utilizados", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "US-010: identifica os supermercados com maior utilização da plataforma no período")
    public ResponseEntity<List<UtilizacaoSupermercadoResponse>> maisUtilizados(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        return ResponseEntity.ok(maisUtilizadosResponse(inicio, fim));
    }

    // US-222/RN-014: mesmo relatório de "mais utilizados", exportado em CSV, com os mesmos filtros
    // de período. Rota própria (em vez de negociação por Accept) para não colidir com a rota JSON
    // acima quando o cliente não envia Accept.
    @GetMapping(path = "/mais-utilizados/csv", produces = "text/csv")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "US-222: exporta em CSV os supermercados com maior utilização da plataforma no período")
    public ResponseEntity<byte[]> maisUtilizadosCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        byte[] csv = RelatorioCsvWriter.paraUtilizacao(maisUtilizadosResponse(inicio, fim));
        return respostaCsv(csv, "mais-utilizados.csv");
    }

    @GetMapping(path = "/baixa-utilizacao", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "US-011: identifica supermercados inativos ou com baixa utilização no período")
    public ResponseEntity<ResultadoBaixaUtilizacaoResponse> baixaUtilizacao(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        return ResponseEntity.ok(baixaUtilizacaoResponse(inicio, fim));
    }

    // US-222/RN-014: mesmo relatório de "baixa utilização", exportado em CSV.
    @GetMapping(path = "/baixa-utilizacao/csv", produces = "text/csv")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "US-222: exporta em CSV os supermercados inativos ou com baixa utilização no período")
    public ResponseEntity<byte[]> baixaUtilizacaoCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        byte[] csv = RelatorioCsvWriter.paraBaixaUtilizacao(baixaUtilizacaoResponse(inicio, fim));
        return respostaCsv(csv, "baixa-utilizacao.csv");
    }

    private List<UtilizacaoSupermercadoResponse> maisUtilizadosResponse(Instant inicio, Instant fim) {
        Instant[] periodo = resolverPeriodo(inicio, fim);
        return listarSupermercadosPorUtilizacao.executar(periodo[0], periodo[1]).stream()
                .map(UtilizacaoSupermercadoResponse::from)
                .toList();
    }

    private ResultadoBaixaUtilizacaoResponse baixaUtilizacaoResponse(Instant inicio, Instant fim) {
        Instant[] periodo = resolverPeriodo(inicio, fim);
        return ResultadoBaixaUtilizacaoResponse.from(listarSupermercadosComBaixaUtilizacao.executar(periodo[0], periodo[1]));
    }

    private Instant[] resolverPeriodo(Instant inicio, Instant fim) {
        Instant fimResolvido = fim != null ? fim : Instant.now();
        Instant inicioResolvido = inicio != null ? inicio : fimResolvido.minus(DIAS_PERIODO_PADRAO, ChronoUnit.DAYS);
        return new Instant[] {inicioResolvido, fimResolvido};
    }

    private ResponseEntity<byte[]> respostaCsv(byte[] csv, String nomeArquivo) {
        return ResponseEntity.ok()
                .contentType(TEXT_CSV)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(nomeArquivo).build().toString())
                .body(csv);
    }
}
