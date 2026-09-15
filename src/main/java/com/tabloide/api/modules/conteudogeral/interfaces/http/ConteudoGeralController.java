package com.tabloide.api.modules.conteudogeral.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.conteudogeral.application.ArquivarConteudoGeral;
import com.tabloide.api.modules.conteudogeral.application.BuscarConteudoGeralPorId;
import com.tabloide.api.modules.conteudogeral.application.CriarConteudoGeral;
import com.tabloide.api.modules.conteudogeral.application.DadosConteudoGeral;
import com.tabloide.api.modules.conteudogeral.application.EditarConteudoGeral;
import com.tabloide.api.modules.conteudogeral.application.ListarConteudosGerais;
import com.tabloide.api.modules.conteudogeral.application.PublicarConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.interfaces.http.dto.ConteudoGeralResponse;
import com.tabloide.api.modules.conteudogeral.interfaces.http.dto.CriarConteudoGeralRequest;
import com.tabloide.api.modules.conteudogeral.interfaces.http.dto.EditarConteudoGeralRequest;
import com.tabloide.api.modules.conteudogeral.interfaces.http.dto.TransicaoConteudoGeralRequest;
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
@RequestMapping(path = "/api/conteudos-gerais", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "ConteudoGeral")
public class ConteudoGeralController {

    private final CriarConteudoGeral criarConteudoGeral;
    private final EditarConteudoGeral editarConteudoGeral;
    private final PublicarConteudoGeral publicarConteudoGeral;
    private final ArquivarConteudoGeral arquivarConteudoGeral;
    private final ListarConteudosGerais listarConteudosGerais;
    private final BuscarConteudoGeralPorId buscarConteudoGeralPorId;

    public ConteudoGeralController(
            CriarConteudoGeral criarConteudoGeral,
            EditarConteudoGeral editarConteudoGeral,
            PublicarConteudoGeral publicarConteudoGeral,
            ArquivarConteudoGeral arquivarConteudoGeral,
            ListarConteudosGerais listarConteudosGerais,
            BuscarConteudoGeralPorId buscarConteudoGeralPorId
    ) {
        this.criarConteudoGeral = criarConteudoGeral;
        this.editarConteudoGeral = editarConteudoGeral;
        this.publicarConteudoGeral = publicarConteudoGeral;
        this.arquivarConteudoGeral = arquivarConteudoGeral;
        this.listarConteudosGerais = listarConteudosGerais;
        this.buscarConteudoGeralPorId = buscarConteudoGeralPorId;
    }

    @GetMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "CONTEUDOS_GERAIS_CONSULTADOS", entidade = "ConteudoGeral")
    @Operation(summary = "Lista os conteúdos gerais da plataforma, paginado e com filtro opcional por tipo")
    public ResponseEntity<PaginaResponse<ConteudoGeralResponse>> listar(
            @RequestParam(required = false) TipoConteudoGeral tipo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        Pagina<ConteudoGeral> resultado = listarConteudosGerais.executar(tipo, pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, ConteudoGeralResponse::from));
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "CONTEUDO_GERAL_CONSULTADO", entidade = "ConteudoGeral", paramEntidadeId = "id")
    @Operation(summary = "Consulta um conteúdo geral da plataforma")
    public ResponseEntity<ConteudoGeralResponse> buscarPorId(@PathVariable Long id) {
        ConteudoGeral conteudoGeral = buscarConteudoGeralPorId.executar(id);
        return ResponseEntity.ok(ConteudoGeralResponse.from(conteudoGeral));
    }

    @PostMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Cria um conteúdo geral da plataforma em rascunho")
    public ResponseEntity<ConteudoGeralResponse> criar(@Valid @RequestBody CriarConteudoGeralRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        ConteudoGeral conteudoGeral = criarConteudoGeral.executar(
                new DadosConteudoGeral(request.tipo(), request.titulo(), request.corpo()), ator.usuarioId(), ator.perfil()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ConteudoGeralResponse.from(conteudoGeral));
    }

    @PatchMapping("/{id}")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Edita título e corpo de um conteúdo geral em rascunho")
    public ResponseEntity<ConteudoGeralResponse> editar(@PathVariable Long id, @Valid @RequestBody EditarConteudoGeralRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        ConteudoGeral conteudoGeral = editarConteudoGeral.executar(
                id, request.versao(), request.titulo(), request.corpo(), ator.usuarioId(), ator.perfil()
        );
        return ResponseEntity.ok(ConteudoGeralResponse.from(conteudoGeral));
    }

    @PostMapping("/{id}/publicacao")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Publica um conteúdo geral em rascunho; arquiva a versão publicada anterior do mesmo tipo, se houver")
    public ResponseEntity<ConteudoGeralResponse> publicar(@PathVariable Long id, @Valid @RequestBody TransicaoConteudoGeralRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        ConteudoGeral conteudoGeral = publicarConteudoGeral.executar(id, request.versao(), ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(ConteudoGeralResponse.from(conteudoGeral));
    }

    @PostMapping("/{id}/arquivamento")
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Arquiva um conteúdo geral publicado")
    public ResponseEntity<ConteudoGeralResponse> arquivar(@PathVariable Long id, @Valid @RequestBody TransicaoConteudoGeralRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        ConteudoGeral conteudoGeral = arquivarConteudoGeral.executar(id, request.versao(), ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(ConteudoGeralResponse.from(conteudoGeral));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
