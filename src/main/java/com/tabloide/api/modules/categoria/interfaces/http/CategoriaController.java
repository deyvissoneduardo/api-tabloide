package com.tabloide.api.modules.categoria.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.categoria.application.AtivarCategoria;
import com.tabloide.api.modules.categoria.application.CadastrarCategoria;
import com.tabloide.api.modules.categoria.application.DadosCategoria;
import com.tabloide.api.modules.categoria.application.DesativarCategoria;
import com.tabloide.api.modules.categoria.application.EditarCategoria;
import com.tabloide.api.modules.categoria.domain.Categoria;
import com.tabloide.api.modules.categoria.interfaces.http.dto.CadastrarCategoriaRequest;
import com.tabloide.api.modules.categoria.interfaces.http.dto.CategoriaResponse;
import com.tabloide.api.modules.categoria.interfaces.http.dto.EditarCategoriaRequest;
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
@RequestMapping(path = "/api/supermercados/{supermercadoId}/categorias", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Categoria")
public class CategoriaController {

    private final CadastrarCategoria cadastrarCategoria;
    private final EditarCategoria editarCategoria;
    private final AtivarCategoria ativarCategoria;
    private final DesativarCategoria desativarCategoria;

    public CategoriaController(
            CadastrarCategoria cadastrarCategoria,
            EditarCategoria editarCategoria,
            AtivarCategoria ativarCategoria,
            DesativarCategoria desativarCategoria
    ) {
        this.cadastrarCategoria = cadastrarCategoria;
        this.editarCategoria = editarCategoria;
        this.ativarCategoria = ativarCategoria;
        this.desativarCategoria = desativarCategoria;
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Cadastra uma categoria no supermercado")
    public ResponseEntity<CategoriaResponse> cadastrar(@PathVariable Long supermercadoId, @Valid @RequestBody CadastrarCategoriaRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Categoria categoria = cadastrarCategoria.executar(
                supermercadoId,
                new DadosCategoria(request.nome(), request.descricao()),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponse.from(categoria));
    }

    @PatchMapping("/{id}")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Edita uma categoria do supermercado")
    public ResponseEntity<CategoriaResponse> editar(
            @PathVariable Long supermercadoId, @PathVariable Long id, @Valid @RequestBody EditarCategoriaRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Categoria categoria = editarCategoria.executar(
                supermercadoId,
                id,
                request.versao(),
                new DadosCategoria(request.nome(), request.descricao()),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.ok(CategoriaResponse.from(categoria));
    }

    @PostMapping("/{id}/ativacao")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Ativa (reativa) uma categoria desativada")
    public ResponseEntity<CategoriaResponse> ativar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Categoria categoria = ativarCategoria.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(CategoriaResponse.from(categoria));
    }

    @PostMapping("/{id}/desativacao")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Desativa uma categoria ativa")
    public ResponseEntity<CategoriaResponse> desativar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Categoria categoria = desativarCategoria.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(CategoriaResponse.from(categoria));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
