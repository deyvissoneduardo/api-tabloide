package com.tabloide.api.modules.campanha.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.campanha.application.BuscarCampanhaPorId;
import com.tabloide.api.modules.campanha.application.CadastrarCampanha;
import com.tabloide.api.modules.campanha.application.CancelarCampanha;
import com.tabloide.api.modules.campanha.application.CompararCampanhas;
import com.tabloide.api.modules.campanha.application.DadosCampanha;
import com.tabloide.api.modules.campanha.application.EditarCampanha;
import com.tabloide.api.modules.campanha.application.ListarCampanhasPorSupermercado;
import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.interfaces.http.dto.CadastrarCampanhaRequest;
import com.tabloide.api.modules.campanha.interfaces.http.dto.CampanhaResponse;
import com.tabloide.api.modules.campanha.interfaces.http.dto.ComparacaoCampanhaResponse;
import com.tabloide.api.modules.campanha.interfaces.http.dto.EditarCampanhaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
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
@RequestMapping(path = "/api/supermercados/{supermercadoId}/campanhas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Campanha")
public class CampanhaController {

    private final CadastrarCampanha cadastrarCampanha;
    private final EditarCampanha editarCampanha;
    private final CancelarCampanha cancelarCampanha;
    private final ListarCampanhasPorSupermercado listarCampanhasPorSupermercado;
    private final BuscarCampanhaPorId buscarCampanhaPorId;
    private final CompararCampanhas compararCampanhas;

    public CampanhaController(
            CadastrarCampanha cadastrarCampanha,
            EditarCampanha editarCampanha,
            CancelarCampanha cancelarCampanha,
            ListarCampanhasPorSupermercado listarCampanhasPorSupermercado,
            BuscarCampanhaPorId buscarCampanhaPorId,
            CompararCampanhas compararCampanhas
    ) {
        this.cadastrarCampanha = cadastrarCampanha;
        this.editarCampanha = editarCampanha;
        this.cancelarCampanha = cancelarCampanha;
        this.listarCampanhasPorSupermercado = listarCampanhasPorSupermercado;
        this.buscarCampanhaPorId = buscarCampanhaPorId;
        this.compararCampanhas = compararCampanhas;
    }

    @GetMapping
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR, Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "CAMPANHAS_CONSULTADAS", entidade = "Campanha", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Lista as campanhas do supermercado, paginado")
    public ResponseEntity<PaginaResponse<CampanhaResponse>> listar(
            @PathVariable Long supermercadoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Pagina<Campanha> resultado = listarCampanhasPorSupermercado.executar(supermercadoId, ator.supermercadoId(), pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, CampanhaResponse::from));
    }

    @GetMapping("/comparacao")
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR, Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "CAMPANHAS_COMPARADAS", entidade = "Campanha", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Compara campanhas do mesmo supermercado lado a lado (período, estado, lojas e ofertas)")
    public ResponseEntity<List<ComparacaoCampanhaResponse>> comparar(
            @PathVariable Long supermercadoId,
            @RequestParam Set<Long> ids
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        List<Campanha> campanhas = compararCampanhas.executar(supermercadoId, ids, ator.supermercadoId());
        return ResponseEntity.ok(campanhas.stream().map(ComparacaoCampanhaResponse::from).toList());
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR, Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "CAMPANHA_CONSULTADA", entidade = "Campanha", paramEntidadeId = "id", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Consulta uma campanha do supermercado")
    public ResponseEntity<CampanhaResponse> buscarPorId(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Campanha campanha = buscarCampanhaPorId.executar(supermercadoId, id, ator.supermercadoId());
        return ResponseEntity.ok(CampanhaResponse.from(campanha));
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Cadastra uma campanha, associada a uma ou mais lojas e, opcionalmente, ofertas")
    public ResponseEntity<CampanhaResponse> cadastrar(@PathVariable Long supermercadoId, @Valid @RequestBody CadastrarCampanhaRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Campanha campanha = cadastrarCampanha.executar(
                supermercadoId,
                new DadosCampanha(
                        request.nome(), request.descricao(), request.lojaIds(), request.ofertaIds(),
                        request.inicio(), request.fim(), request.confirmarPublicacao()
                ),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(CampanhaResponse.from(campanha));
    }

    @PatchMapping("/{id}")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Edita nome, descrição, período, lojas e ofertas de uma campanha não finalizada")
    public ResponseEntity<CampanhaResponse> editar(
            @PathVariable Long supermercadoId, @PathVariable Long id, @Valid @RequestBody EditarCampanhaRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Campanha campanha = editarCampanha.executar(
                supermercadoId, id, request.versao(),
                new DadosCampanha(request.nome(), request.descricao(), request.lojaIds(), request.ofertaIds(), request.inicio(), request.fim(), false),
                ator.usuarioId(), ator.perfil(), ator.supermercadoId()
        );
        return ResponseEntity.ok(CampanhaResponse.from(campanha));
    }

    @PostMapping("/{id}/cancelamento")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Cancela definitivamente uma campanha")
    public ResponseEntity<CampanhaResponse> cancelar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Campanha campanha = cancelarCampanha.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(CampanhaResponse.from(campanha));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
