package com.tabloide.api.modules.oferta.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.oferta.application.AtivarOferta;
import com.tabloide.api.modules.oferta.application.CadastrarOferta;
import com.tabloide.api.modules.oferta.application.CancelarOferta;
import com.tabloide.api.modules.oferta.application.CopiarOferta;
import com.tabloide.api.modules.oferta.application.DadosOferta;
import com.tabloide.api.modules.oferta.application.DesativarOferta;
import com.tabloide.api.modules.oferta.application.EditarOferta;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.interfaces.http.dto.CadastrarOfertaRequest;
import com.tabloide.api.modules.oferta.interfaces.http.dto.EditarOfertaRequest;
import com.tabloide.api.modules.oferta.interfaces.http.dto.OfertaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/supermercados/{supermercadoId}/ofertas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Oferta")
public class OfertaController {

    private final CadastrarOferta cadastrarOferta;
    private final EditarOferta editarOferta;
    private final CancelarOferta cancelarOferta;
    private final AtivarOferta ativarOferta;
    private final DesativarOferta desativarOferta;
    private final CopiarOferta copiarOferta;

    public OfertaController(
            CadastrarOferta cadastrarOferta,
            EditarOferta editarOferta,
            CancelarOferta cancelarOferta,
            AtivarOferta ativarOferta,
            DesativarOferta desativarOferta,
            CopiarOferta copiarOferta
    ) {
        this.cadastrarOferta = cadastrarOferta;
        this.editarOferta = editarOferta;
        this.cancelarOferta = cancelarOferta;
        this.ativarOferta = ativarOferta;
        this.desativarOferta = desativarOferta;
        this.copiarOferta = copiarOferta;
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

    @PatchMapping("/{id}")
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR})
    @Operation(summary = "Edita produto, lojas, preços, período e condições de uma oferta não finalizada")
    public ResponseEntity<OfertaResponse> editar(
            @PathVariable Long supermercadoId, @PathVariable Long id, @Valid @RequestBody EditarOfertaRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Oferta oferta = editarOferta.executar(
                supermercadoId, id, request.versao(),
                new DadosOferta(
                        request.produtoId(), request.lojaIds(), request.precoNormal(), request.precoPromocional(),
                        request.inicio(), request.fim(), request.condicoes(), false
                ),
                ator.usuarioId(), ator.perfil(), ator.supermercadoId()
        );
        return ResponseEntity.ok(OfertaResponse.from(oferta));
    }

    @PostMapping("/{id}/cancelamento")
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR})
    @Operation(summary = "Cancela definitivamente uma oferta")
    public ResponseEntity<OfertaResponse> cancelar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Oferta oferta = cancelarOferta.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(OfertaResponse.from(oferta));
    }

    @PostMapping("/{id}/ativacao")
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR})
    @Operation(summary = "Ativa (reativa) uma oferta desativada, respeitando o período de vigência")
    public ResponseEntity<OfertaResponse> ativar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Oferta oferta = ativarOferta.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(OfertaResponse.from(oferta));
    }

    @PostMapping("/{id}/desativacao")
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR})
    @Operation(summary = "Desativa uma oferta agendada ou vigente")
    public ResponseEntity<OfertaResponse> desativar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Oferta oferta = desativarOferta.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(OfertaResponse.from(oferta));
    }

    @PostMapping("/{id}/copia")
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR})
    @Operation(summary = "Copia uma oferta existente para um novo rascunho, sem métricas, id ou estado do original")
    public ResponseEntity<OfertaResponse> copiar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Oferta copia = copiarOferta.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(OfertaResponse.from(copia));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
