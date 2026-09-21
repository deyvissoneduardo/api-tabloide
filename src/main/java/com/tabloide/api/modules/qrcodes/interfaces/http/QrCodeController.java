package com.tabloide.api.modules.qrcodes.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.qrcodes.application.AtivarQrCode;
import com.tabloide.api.modules.qrcodes.application.BuscarQrCodePorId;
import com.tabloide.api.modules.qrcodes.application.ContarAcessosDeQrCodes;
import com.tabloide.api.modules.qrcodes.application.DadosQrCode;
import com.tabloide.api.modules.qrcodes.application.DesativarQrCode;
import com.tabloide.api.modules.qrcodes.application.GerarImagemQrCode;
import com.tabloide.api.modules.qrcodes.application.GerarQrCode;
import com.tabloide.api.modules.qrcodes.application.ListarQrCodesPorLoja;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.interfaces.http.dto.CadastrarQrCodeRequest;
import com.tabloide.api.modules.qrcodes.interfaces.http.dto.QrCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/supermercados/{supermercadoId}/lojas/{lojaId}/qrcodes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "QrCode")
public class QrCodeController {

    private static final MediaType IMAGE_SVG = MediaType.valueOf("image/svg+xml");
    // RN-007 (Analytics): período padrão de consulta é de 30 dias quando não informado.
    private static final int DIAS_PERIODO_PADRAO = 30;

    private final GerarQrCode gerarQrCode;
    private final ListarQrCodesPorLoja listarQrCodesPorLoja;
    private final BuscarQrCodePorId buscarQrCodePorId;
    private final AtivarQrCode ativarQrCode;
    private final DesativarQrCode desativarQrCode;
    private final GerarImagemQrCode gerarImagemQrCode;
    private final ContarAcessosDeQrCodes contarAcessosDeQrCodes;

    public QrCodeController(
            GerarQrCode gerarQrCode,
            ListarQrCodesPorLoja listarQrCodesPorLoja,
            BuscarQrCodePorId buscarQrCodePorId,
            AtivarQrCode ativarQrCode,
            DesativarQrCode desativarQrCode,
            GerarImagemQrCode gerarImagemQrCode,
            ContarAcessosDeQrCodes contarAcessosDeQrCodes
    ) {
        this.gerarQrCode = gerarQrCode;
        this.listarQrCodesPorLoja = listarQrCodesPorLoja;
        this.buscarQrCodePorId = buscarQrCodePorId;
        this.ativarQrCode = ativarQrCode;
        this.desativarQrCode = desativarQrCode;
        this.gerarImagemQrCode = gerarImagemQrCode;
        this.contarAcessosDeQrCodes = contarAcessosDeQrCodes;
    }

    @GetMapping
    @RequerPerfil({Perfil.DONO, Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "QRCODES_CONSULTADOS", entidade = "QrCode", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Lista os QR Codes de uma loja, paginado, com a quantidade de acessos de cada um no período")
    public ResponseEntity<PaginaResponse<QrCodeResponse>> listar(
            @PathVariable Long supermercadoId,
            @PathVariable Long lojaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Pagina<QrCode> resultado = listarQrCodesPorLoja.executar(supermercadoId, lojaId, ator.perfil(), ator.supermercadoId(), pagina, tamanho);
        Instant[] periodo = resolverPeriodo(inicio, fim);
        Map<Long, Long> acessosPorQrCode = contarAcessosDeQrCodes.executar(
                resultado.itens().stream().map(QrCode::id).toList(), periodo[0], periodo[1]);
        return ResponseEntity.ok(PaginaResponse.from(
                resultado, qrCode -> QrCodeResponse.from(qrCode, acessosPorQrCode.getOrDefault(qrCode.id(), 0L))));
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.DONO, Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "QRCODE_CONSULTADO", entidade = "QrCode", paramEntidadeId = "id", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Consulta os dados, o estado e a quantidade de acessos no período de um QR Code")
    public ResponseEntity<QrCodeResponse> buscarPorId(
            @PathVariable Long supermercadoId,
            @PathVariable Long lojaId,
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        QrCode qrCode = buscarQrCodePorId.executar(supermercadoId, id, ator.perfil(), ator.supermercadoId());
        Instant[] periodo = resolverPeriodo(inicio, fim);
        Map<Long, Long> acessos = contarAcessosDeQrCodes.executar(List.of(qrCode.id()), periodo[0], periodo[1]);
        return ResponseEntity.ok(QrCodeResponse.from(qrCode, acessos.getOrDefault(qrCode.id(), 0L)));
    }

    private Instant[] resolverPeriodo(Instant inicio, Instant fim) {
        Instant fimResolvido = fim != null ? fim : Instant.now();
        Instant inicioResolvido = inicio != null ? inicio : fimResolvido.minus(DIAS_PERIODO_PADRAO, ChronoUnit.DAYS);
        return new Instant[] {inicioResolvido, fimResolvido};
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Gera um QR Code para a loja")
    public ResponseEntity<QrCodeResponse> gerar(
            @PathVariable Long supermercadoId,
            @PathVariable Long lojaId,
            @Valid @RequestBody CadastrarQrCodeRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        QrCode qrCode = gerarQrCode.executar(
                supermercadoId, lojaId, new DadosQrCode(request.nome()), ator.usuarioId(), ator.perfil(), ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(QrCodeResponse.from(qrCode));
    }

    @PostMapping("/{id}/ativacao")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Ativa (reativa) um QR Code desativado")
    public ResponseEntity<QrCodeResponse> ativar(@PathVariable Long supermercadoId, @PathVariable Long lojaId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        QrCode qrCode = ativarQrCode.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(QrCodeResponse.from(qrCode));
    }

    @PostMapping("/{id}/desativacao")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Desativa um QR Code ativo")
    public ResponseEntity<QrCodeResponse> desativar(@PathVariable Long supermercadoId, @PathVariable Long lojaId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        QrCode qrCode = desativarQrCode.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(QrCodeResponse.from(qrCode));
    }

    @GetMapping(path = "/{id}/imagem", produces = {MediaType.IMAGE_PNG_VALUE, "image/svg+xml"})
    @RequerPerfil({Perfil.DONO, Perfil.SUPER_ADMIN})
    @Operation(summary = "Baixa a imagem do QR Code em PNG ou SVG")
    public ResponseEntity<byte[]> baixarImagem(
            @PathVariable Long supermercadoId,
            @PathVariable Long lojaId,
            @PathVariable Long id,
            @RequestParam(defaultValue = "png") String formato
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        QrCode qrCode = buscarQrCodePorId.executar(supermercadoId, id, ator.perfil(), ator.supermercadoId());

        if ("svg".equalsIgnoreCase(formato)) {
            return ResponseEntity.ok().contentType(IMAGE_SVG).body(gerarImagemQrCode.paraSvg(qrCode));
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(gerarImagemQrCode.paraPng(qrCode));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
