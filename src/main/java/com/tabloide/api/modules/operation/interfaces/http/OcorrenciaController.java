package com.tabloide.api.modules.operation.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.operation.application.AbrirOcorrencia;
import com.tabloide.api.modules.operation.application.AdicionarComentarioOcorrencia;
import com.tabloide.api.modules.operation.application.BuscarOcorrenciaPorId;
import com.tabloide.api.modules.operation.application.DadosAbrirOcorrencia;
import com.tabloide.api.modules.operation.application.ListarOcorrencias;
import com.tabloide.api.modules.operation.application.OcorrenciaComHistorico;
import com.tabloide.api.modules.operation.application.TransicionarEstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.FiltroOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.SeveridadeOcorrencia;
import com.tabloide.api.modules.operation.interfaces.http.dto.AbrirOcorrenciaRequest;
import com.tabloide.api.modules.operation.interfaces.http.dto.AdicionarComentarioRequest;
import com.tabloide.api.modules.operation.interfaces.http.dto.OcorrenciaDetalheResponse;
import com.tabloide.api.modules.operation.interfaces.http.dto.OcorrenciaResponse;
import com.tabloide.api.modules.operation.interfaces.http.dto.TransicionarEstadoOcorrenciaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/ocorrencias", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Ocorrência")
public class OcorrenciaController {

    private final AbrirOcorrencia abrirOcorrencia;
    private final TransicionarEstadoOcorrencia transicionarEstadoOcorrencia;
    private final AdicionarComentarioOcorrencia adicionarComentarioOcorrencia;
    private final ListarOcorrencias listarOcorrencias;
    private final BuscarOcorrenciaPorId buscarOcorrenciaPorId;

    public OcorrenciaController(
            AbrirOcorrencia abrirOcorrencia,
            TransicionarEstadoOcorrencia transicionarEstadoOcorrencia,
            AdicionarComentarioOcorrencia adicionarComentarioOcorrencia,
            ListarOcorrencias listarOcorrencias,
            BuscarOcorrenciaPorId buscarOcorrenciaPorId
    ) {
        this.abrirOcorrencia = abrirOcorrencia;
        this.transicionarEstadoOcorrencia = transicionarEstadoOcorrencia;
        this.adicionarComentarioOcorrencia = adicionarComentarioOcorrencia;
        this.listarOcorrencias = listarOcorrencias;
        this.buscarOcorrenciaPorId = buscarOcorrenciaPorId;
    }

    @PostMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Abre uma nova ocorrência de suporte")
    public ResponseEntity<OcorrenciaResponse> abrir(@Valid @RequestBody AbrirOcorrenciaRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Ocorrencia ocorrencia = abrirOcorrencia.executar(
                new DadosAbrirOcorrencia(request.titulo(), request.descricao(), request.severidade(), request.supermercadoId(), request.responsavelId()),
                ator.usuarioId(),
                ator.perfil()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(OcorrenciaResponse.from(ocorrencia));
    }

    @PatchMapping("/{id}/estado")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Transiciona o estado de uma ocorrência (ABERTA -> EM_ANALISE -> RESOLVIDA -> ENCERRADA)")
    public ResponseEntity<OcorrenciaResponse> transicionarEstado(@PathVariable Long id, @Valid @RequestBody TransicionarEstadoOcorrenciaRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Ocorrencia ocorrencia = transicionarEstadoOcorrencia.executar(
                id, request.versao(), request.novoEstado(), request.comentario(), ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(OcorrenciaResponse.from(ocorrencia));
    }

    @PostMapping("/{id}/comentarios")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Adiciona um comentário ao histórico da ocorrência, sem alterar o estado")
    public ResponseEntity<OcorrenciaResponse> adicionarComentario(@PathVariable Long id, @Valid @RequestBody AdicionarComentarioRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Ocorrencia ocorrencia = adicionarComentarioOcorrencia.executar(id, request.comentario(), ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(OcorrenciaResponse.from(ocorrencia));
    }

    @GetMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "OCORRENCIAS_CONSULTADAS", entidade = "Ocorrencia", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Lista ocorrências, com filtros opcionais de status, severidade e supermercado")
    public ResponseEntity<PaginaResponse<OcorrenciaResponse>> listar(
            @RequestParam(required = false) EstadoOcorrencia status,
            @RequestParam(required = false) SeveridadeOcorrencia severidade,
            @RequestParam(required = false) Long supermercadoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        FiltroOcorrencia filtro = new FiltroOcorrencia(status, severidade, supermercadoId);
        Pagina<Ocorrencia> resultado = listarOcorrencias.executar(filtro, pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, OcorrenciaResponse::from));
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "OCORRENCIA_CONSULTADA", entidade = "Ocorrencia", paramEntidadeId = "id")
    @Operation(summary = "Consulta uma ocorrência pelo identificador, com o histórico de comentários e transições")
    public ResponseEntity<OcorrenciaDetalheResponse> buscarPorId(@PathVariable Long id) {
        OcorrenciaComHistorico resultado = buscarOcorrenciaPorId.executar(id);
        return ResponseEntity.ok(OcorrenciaDetalheResponse.from(resultado));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
