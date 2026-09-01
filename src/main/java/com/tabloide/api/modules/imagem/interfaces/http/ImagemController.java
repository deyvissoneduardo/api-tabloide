package com.tabloide.api.modules.imagem.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.imagem.application.DadosImagem;
import com.tabloide.api.modules.imagem.application.ExcluirImagem;
import com.tabloide.api.modules.imagem.application.ListarImagens;
import com.tabloide.api.modules.imagem.application.RegistrarImagem;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.interfaces.http.dto.ImagemResponse;
import com.tabloide.api.modules.imagem.interfaces.http.dto.RegistrarImagemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/imagens", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Imagem")
public class ImagemController {

    private final RegistrarImagem registrarImagem;
    private final ExcluirImagem excluirImagem;
    private final ListarImagens listarImagens;

    public ImagemController(RegistrarImagem registrarImagem, ExcluirImagem excluirImagem, ListarImagens listarImagens) {
        this.registrarImagem = registrarImagem;
        this.excluirImagem = excluirImagem;
        this.listarImagens = listarImagens;
    }

    @GetMapping
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR, Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "IMAGENS_CONSULTADAS", entidade = "Imagem", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Lista a biblioteca de imagens de um supermercado, pesquisável por nome")
    public ResponseEntity<PaginaResponse<ImagemResponse>> listar(
            @RequestParam Long supermercadoId,
            @RequestParam(required = false) String nomeBusca,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Pagina<Imagem> resultado = listarImagens.executar(supermercadoId, ator.perfil(), ator.supermercadoId(), nomeBusca, pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, ImagemResponse::from));
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Registra uma imagem no supermercado do DONO autenticado, respeitando a cota do plano")
    public ResponseEntity<ImagemResponse> registrar(@Valid @RequestBody RegistrarImagemRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Imagem imagem = registrarImagem.executar(
                ator.supermercadoId(),
                new DadosImagem(
                        request.nomeBusca(), request.tipoVinculo(), request.vinculoId(),
                        request.urlOuChave(), request.formato(), request.tamanhoBytes()
                ),
                ator.usuarioId(),
                ator.perfil()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ImagemResponse.from(imagem));
    }

    @DeleteMapping("/{id}")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Exclui uma imagem não vinculada, liberando a cota do plano")
    public ResponseEntity<ImagemResponse> excluir(@PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Imagem imagem = excluirImagem.executar(id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(ImagemResponse.from(imagem));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
