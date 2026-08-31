package com.tabloide.api.modules.plano.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.plano.application.BuscarPlanoPorId;
import com.tabloide.api.modules.plano.application.CriarPlano;
import com.tabloide.api.modules.plano.application.DadosPlano;
import com.tabloide.api.modules.plano.application.EditarPlano;
import com.tabloide.api.modules.plano.application.ExcluirPlano;
import com.tabloide.api.modules.plano.application.ListarPlanos;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.interfaces.http.dto.CriarPlanoRequest;
import com.tabloide.api.modules.plano.interfaces.http.dto.EditarPlanoRequest;
import com.tabloide.api.modules.plano.interfaces.http.dto.PlanoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/planos", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Plano")
public class PlanoController {

    private final CriarPlano criarPlano;
    private final EditarPlano editarPlano;
    private final ExcluirPlano excluirPlano;
    private final BuscarPlanoPorId buscarPlanoPorId;
    private final ListarPlanos listarPlanos;

    public PlanoController(
            CriarPlano criarPlano,
            EditarPlano editarPlano,
            ExcluirPlano excluirPlano,
            BuscarPlanoPorId buscarPlanoPorId,
            ListarPlanos listarPlanos
    ) {
        this.criarPlano = criarPlano;
        this.editarPlano = editarPlano;
        this.excluirPlano = excluirPlano;
        this.buscarPlanoPorId = buscarPlanoPorId;
        this.listarPlanos = listarPlanos;
    }

    @GetMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "PLANOS_CONSULTADOS", entidade = "Plano")
    @Operation(summary = "Lista planos comerciais do catálogo, paginado")
    public ResponseEntity<PaginaResponse<PlanoResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        Pagina<Plano> resultado = listarPlanos.executar(pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, PlanoResponse::from));
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "PLANO_CONSULTADO", entidade = "Plano", paramEntidadeId = "id")
    @Operation(summary = "Consulta um plano comercial do catálogo")
    public ResponseEntity<PlanoResponse> buscarPorId(@PathVariable Long id) {
        Plano plano = buscarPlanoPorId.executar(id);
        return ResponseEntity.ok(PlanoResponse.from(plano));
    }

    @PostMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Cria um plano comercial no catálogo")
    public ResponseEntity<PlanoResponse> criar(@Valid @RequestBody CriarPlanoRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Plano plano = criarPlano.executar(
                new DadosPlano(request.nome(), request.validadeDias(), request.valor(), request.limiteFotos(), request.limiteLojas()),
                ator.usuarioId(),
                ator.perfil()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(PlanoResponse.from(plano));
    }

    @PatchMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Edita um plano comercial; alterações valem só para associações e renovações futuras")
    public ResponseEntity<PlanoResponse> editar(@PathVariable Long id, @Valid @RequestBody EditarPlanoRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Plano plano = editarPlano.executar(
                id,
                request.versao(),
                new DadosPlano(request.nome(), request.validadeDias(), request.valor(), request.limiteFotos(), request.limiteLojas()),
                ator.usuarioId(),
                ator.perfil()
        );
        return ResponseEntity.ok(PlanoResponse.from(plano));
    }

    @DeleteMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Exclui logicamente um plano comercial do catálogo")
    public ResponseEntity<PlanoResponse> excluir(@PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Plano plano = excluirPlano.executar(id, ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(PlanoResponse.from(plano));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
