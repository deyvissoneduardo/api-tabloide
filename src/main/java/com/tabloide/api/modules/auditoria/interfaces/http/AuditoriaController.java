package com.tabloide.api.modules.auditoria.interfaces.http;

import com.tabloide.api.modules.auditoria.application.BuscarAuditoriaPorId;
import com.tabloide.api.modules.auditoria.application.ListarAuditoria;
import com.tabloide.api.modules.auditoria.domain.FiltroAuditoria;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.auditoria.interfaces.http.dto.RegistroAuditoriaResponse;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auditoria", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Auditoria")
public class AuditoriaController {

    private final ListarAuditoria listarAuditoria;
    private final BuscarAuditoriaPorId buscarAuditoriaPorId;

    public AuditoriaController(ListarAuditoria listarAuditoria, BuscarAuditoriaPorId buscarAuditoriaPorId) {
        this.listarAuditoria = listarAuditoria;
        this.buscarAuditoriaPorId = buscarAuditoriaPorId;
    }

    @GetMapping
    @RequerPerfil({Perfil.SUPER_ADMIN, Perfil.DONO})
    @AuditarConsulta(acao = "AUDITORIA_CONSULTADA", entidade = "RegistroAuditoria", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Lista registros de auditoria: Super Admin vê todos ou filtra por supermercadoId, "
            + "DONO só os do próprio supermercado (parâmetro supermercadoId é ignorado para DONO)")
    public ResponseEntity<PaginaResponse<RegistroAuditoriaResponse>> listar(
            @RequestParam(required = false) Long atorId,
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) Instant dataInicio,
            @RequestParam(required = false) Instant dataFim,
            @RequestParam(required = false) Long supermercadoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        ClaimsSessao solicitante = contextoObrigatorio();
        FiltroAuditoria filtro = new FiltroAuditoria(atorId, acao, entidade, dataInicio, dataFim, supermercadoId);
        Pagina<RegistroAuditoria> resultado = listarAuditoria.executar(solicitante.perfil(), solicitante.supermercadoId(), filtro, pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, RegistroAuditoriaResponse::from));
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN, Perfil.DONO})
    @AuditarConsulta(acao = "AUDITORIA_CONSULTADA", entidade = "RegistroAuditoria", paramEntidadeId = "id")
    @Operation(summary = "Consulta um registro de auditoria pelo identificador, respeitando o escopo do perfil")
    public ResponseEntity<RegistroAuditoriaResponse> buscarPorId(@PathVariable Long id) {
        ClaimsSessao solicitante = contextoObrigatorio();
        RegistroAuditoria registro = buscarAuditoriaPorId.executar(id, solicitante.perfil(), solicitante.supermercadoId());
        return ResponseEntity.ok(RegistroAuditoriaResponse.from(registro));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
