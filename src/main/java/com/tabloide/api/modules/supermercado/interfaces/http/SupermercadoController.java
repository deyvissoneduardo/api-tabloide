package com.tabloide.api.modules.supermercado.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.supermercado.application.AtivarSupermercado;
import com.tabloide.api.modules.supermercado.application.BloquearSupermercado;
import com.tabloide.api.modules.supermercado.application.CadastrarSupermercado;
import com.tabloide.api.modules.supermercado.application.DadosSupermercado;
import com.tabloide.api.modules.supermercado.application.DesativarSupermercado;
import com.tabloide.api.modules.supermercado.application.EditarSupermercado;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    public SupermercadoController(
            CadastrarSupermercado cadastrarSupermercado,
            EditarSupermercado editarSupermercado,
            AtivarSupermercado ativarSupermercado,
            DesativarSupermercado desativarSupermercado,
            BloquearSupermercado bloquearSupermercado
    ) {
        this.cadastrarSupermercado = cadastrarSupermercado;
        this.editarSupermercado = editarSupermercado;
        this.ativarSupermercado = ativarSupermercado;
        this.desativarSupermercado = desativarSupermercado;
        this.bloquearSupermercado = bloquearSupermercado;
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
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Edita os dados cadastrais de um supermercado")
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
                ator.perfil()
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
