package com.tabloide.api.modules.supermercado.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.supermercado.application.AtivarSupermercado;
import com.tabloide.api.modules.supermercado.application.BloquearSupermercado;
import com.tabloide.api.modules.supermercado.application.BuscarSupermercadoPorId;
import com.tabloide.api.modules.supermercado.application.CadastrarSupermercado;
import com.tabloide.api.modules.supermercado.application.DadosSupermercado;
import com.tabloide.api.modules.supermercado.application.DesativarSupermercado;
import com.tabloide.api.modules.supermercado.application.EditarSupermercado;
import com.tabloide.api.modules.supermercado.application.ListarSupermercados;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.interfaces.http.dto.CadastrarSupermercadoRequest;
import com.tabloide.api.modules.supermercado.interfaces.http.dto.EditarSupermercadoRequest;
import com.tabloide.api.modules.supermercado.interfaces.http.dto.SupermercadoResponse;
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
@RequestMapping(path = "/api/supermercados", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Supermercado")
public class SupermercadoController {

    private final CadastrarSupermercado cadastrarSupermercado;
    private final EditarSupermercado editarSupermercado;
    private final AtivarSupermercado ativarSupermercado;
    private final DesativarSupermercado desativarSupermercado;
    private final BloquearSupermercado bloquearSupermercado;
    private final BuscarSupermercadoPorId buscarSupermercadoPorId;
    private final ListarSupermercados listarSupermercados;

    public SupermercadoController(
            CadastrarSupermercado cadastrarSupermercado,
            EditarSupermercado editarSupermercado,
            AtivarSupermercado ativarSupermercado,
            DesativarSupermercado desativarSupermercado,
            BloquearSupermercado bloquearSupermercado,
            BuscarSupermercadoPorId buscarSupermercadoPorId,
            ListarSupermercados listarSupermercados
    ) {
        this.cadastrarSupermercado = cadastrarSupermercado;
        this.editarSupermercado = editarSupermercado;
        this.ativarSupermercado = ativarSupermercado;
        this.desativarSupermercado = desativarSupermercado;
        this.bloquearSupermercado = bloquearSupermercado;
        this.buscarSupermercadoPorId = buscarSupermercadoPorId;
        this.listarSupermercados = listarSupermercados;
    }

    @GetMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "SUPERMERCADOS_CONSULTADOS", entidade = "Supermercado")
    @Operation(summary = "Lista supermercados cadastrados, paginado")
    public ResponseEntity<PaginaResponse<SupermercadoResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        Pagina<Supermercado> resultado = listarSupermercados.executar(pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, SupermercadoResponse::from));
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "SUPERMERCADO_CONSULTADO", entidade = "Supermercado", paramEntidadeId = "id", paramSupermercadoId = "id")
    @Operation(summary = "Consulta os dados cadastrais e o estado de um supermercado")
    public ResponseEntity<SupermercadoResponse> buscarPorId(@PathVariable Long id) {
        Supermercado supermercado = buscarSupermercadoPorId.executar(id);
        return ResponseEntity.ok(SupermercadoResponse.from(supermercado));
    }

    @PostMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Cadastra um supermercado manualmente")
    public ResponseEntity<SupermercadoResponse> cadastrar(@Valid @RequestBody CadastrarSupermercadoRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Supermercado supermercado = cadastrarSupermercado.executar(
                new Cnpj(request.cnpj()),
                new DadosSupermercado(
                        request.razaoSocial(),
                        request.nomeFantasia(),
                        request.emailComercial(),
                        request.telefoneComercial(),
                        new Endereco(request.cep(), request.logradouro(), request.numero(), request.bairro(), request.municipio(), request.uf()),
                        request.complemento(),
                        request.logomarcaUrl(),
                        request.observacoesInternas()
                ),
                ator.usuarioId(),
                ator.perfil()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(SupermercadoResponse.from(supermercado));
    }

    @PatchMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN, Perfil.DONO})
    @Operation(summary = "Edita os dados cadastrais de um supermercado (Super Admin, qualquer um; DONO, apenas o próprio)")
    public ResponseEntity<SupermercadoResponse> editar(@PathVariable Long id, @Valid @RequestBody EditarSupermercadoRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Supermercado supermercado = editarSupermercado.executar(
                id,
                request.versao(),
                new DadosSupermercado(
                        request.razaoSocial(),
                        request.nomeFantasia(),
                        request.emailComercial(),
                        request.telefoneComercial(),
                        new Endereco(request.cep(), request.logradouro(), request.numero(), request.bairro(), request.municipio(), request.uf()),
                        request.complemento(),
                        request.logomarcaUrl(),
                        request.observacoesInternas()
                ),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.ok(SupermercadoResponse.from(supermercado));
    }

    @PostMapping("/{id}/ativacao")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Ativa (reativa) um supermercado bloqueado ou desativado")
    public ResponseEntity<SupermercadoResponse> ativar(@PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Supermercado supermercado = ativarSupermercado.executar(id, ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(SupermercadoResponse.from(supermercado));
    }

    @PostMapping("/{id}/desativacao")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Desativa um supermercado ativo ou bloqueado")
    public ResponseEntity<SupermercadoResponse> desativar(@PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Supermercado supermercado = desativarSupermercado.executar(id, ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(SupermercadoResponse.from(supermercado));
    }

    @PostMapping("/{id}/bloqueio")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Bloqueia o acesso de um supermercado ativo à plataforma")
    public ResponseEntity<SupermercadoResponse> bloquear(@PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Supermercado supermercado = bloquearSupermercado.executar(id, ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(SupermercadoResponse.from(supermercado));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
