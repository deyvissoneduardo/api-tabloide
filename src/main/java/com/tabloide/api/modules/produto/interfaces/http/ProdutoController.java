package com.tabloide.api.modules.produto.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.produto.application.CadastrarProduto;
import com.tabloide.api.modules.produto.application.AssociarCategoriasAoProduto;
import com.tabloide.api.modules.produto.application.DadosProduto;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.interfaces.http.dto.CadastrarProdutoRequest;
import com.tabloide.api.modules.produto.interfaces.http.dto.AssociarCategoriasProdutoRequest;
import com.tabloide.api.modules.produto.interfaces.http.dto.ProdutoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/supermercados/{supermercadoId}/produtos", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Produto")
public class ProdutoController {

    private final CadastrarProduto cadastrarProduto;
    private final AssociarCategoriasAoProduto associarCategoriasAoProduto;

    public ProdutoController(CadastrarProduto cadastrarProduto, AssociarCategoriasAoProduto associarCategoriasAoProduto) {
        this.cadastrarProduto = cadastrarProduto;
        this.associarCategoriasAoProduto = associarCategoriasAoProduto;
    }

    @PutMapping("/{produtoId}/categorias")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Substitui as categorias associadas ao produto")
    public ResponseEntity<ProdutoResponse> associarCategorias(@PathVariable Long supermercadoId,
                                                               @PathVariable Long produtoId,
                                                               @Valid @RequestBody AssociarCategoriasProdutoRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Produto produto = associarCategoriasAoProduto.executar(
                supermercadoId, produtoId, request.versao(), request.categoriaIds(),
                ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(ProdutoResponse.from(produto));
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Cadastra um produto no supermercado, associado a uma ou mais categorias")
    public ResponseEntity<ProdutoResponse> cadastrar(@PathVariable Long supermercadoId, @Valid @RequestBody CadastrarProdutoRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Produto produto = cadastrarProduto.executar(
                supermercadoId,
                new DadosProduto(
                        request.nome(), request.categoriaIds(), request.marca(),
                        request.descricao(), request.peso(), request.unidade(), request.volume()
                ),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.from(produto));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
