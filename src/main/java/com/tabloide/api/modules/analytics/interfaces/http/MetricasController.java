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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/metricas/supermercados", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Metricas")
public class MetricasController {

    // RN-007 (Analytics): período padrão de consulta é de 30 dias quando não informado.
    private static final int DIAS_PERIODO_PADRAO = 30;

    private final ListarSupermercadosPorUtilizacao listarSupermercadosPorUtilizacao;
    private final ListarSupermercadosComBaixaUtilizacao listarSupermercadosComBaixaUtilizacao;

    public MetricasController(
            ListarSupermercadosPorUtilizacao listarSupermercadosPorUtilizacao,
            ListarSupermercadosComBaixaUtilizacao listarSupermercadosComBaixaUtilizacao
    ) {
        this.listarSupermercadosPorUtilizacao = listarSupermercadosPorUtilizacao;
        this.listarSupermercadosComBaixaUtilizacao = listarSupermercadosComBaixaUtilizacao;
    }

    @GetMapping("/mais-utilizados")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "US-010: identifica os supermercados com maior utilização da plataforma no período")
    public ResponseEntity<List<UtilizacaoSupermercadoResponse>> maisUtilizados(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        Instant fimResolvido = fim != null ? fim : Instant.now();
        Instant inicioResolvido = inicio != null ? inicio : fimResolvido.minus(DIAS_PERIODO_PADRAO, ChronoUnit.DAYS);
        List<UtilizacaoSupermercadoResponse> resposta = listarSupermercadosPorUtilizacao.executar(inicioResolvido, fimResolvido).stream()
                .map(UtilizacaoSupermercadoResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/baixa-utilizacao")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "US-011: identifica supermercados inativos ou com baixa utilização no período")
    public ResponseEntity<ResultadoBaixaUtilizacaoResponse> baixaUtilizacao(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        Instant fimResolvido = fim != null ? fim : Instant.now();
        Instant inicioResolvido = inicio != null ? inicio : fimResolvido.minus(DIAS_PERIODO_PADRAO, ChronoUnit.DAYS);
        return ResponseEntity.ok(ResultadoBaixaUtilizacaoResponse.from(
                listarSupermercadosComBaixaUtilizacao.executar(inicioResolvido, fimResolvido)));
    }
}
