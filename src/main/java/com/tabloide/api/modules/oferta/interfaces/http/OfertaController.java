package com.tabloide.api.modules.oferta.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.oferta.application.CadastrarOferta;
import com.tabloide.api.modules.oferta.application.DadosOferta;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.interfaces.http.dto.CadastrarOfertaRequest;
import com.tabloide.api.modules.oferta.interfaces.http.dto.OfertaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/supermercados/{supermercadoId}/ofertas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Oferta")
public class OfertaController {

    private final CadastrarOferta cadastrarOferta;

    public OfertaController(CadastrarOferta cadastrarOferta) {
        this.cadastrarOferta = cadastrarOferta;
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR})
    @Operation(summary = "Cadastra uma oferta de produto, associada a uma ou mais lojas do supermercado")
    public ResponseEntity<OfertaResponse> cadastrar(@PathVariable Long supermercadoId, @Valid @RequestBody CadastrarOfertaRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Oferta oferta = cadastrarOferta.executar(
                supermercadoId,
                new DadosOferta(
                        request.produtoId(), request.lojaIds(), request.precoNormal(), request.precoPromocional(),
                        request.inicio(), request.fim(), request.condicoes(), request.confirmarPublicacao()
                ),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(OfertaResponse.from(oferta));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
