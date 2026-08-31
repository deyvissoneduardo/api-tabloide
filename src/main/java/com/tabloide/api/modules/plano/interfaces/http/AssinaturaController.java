package com.tabloide.api.modules.plano.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.plano.application.AlterarPlanoSupermercado;
import com.tabloide.api.modules.plano.application.AssociarPlano;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.interfaces.http.dto.AssinaturaResponse;
import com.tabloide.api.modules.plano.interfaces.http.dto.AssociarPlanoRequest;
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
@RequestMapping(path = "/api/supermercados/{supermercadoId}/plano", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Plano")
public class AssinaturaController {

    private final AssociarPlano associarPlano;
    private final AlterarPlanoSupermercado alterarPlanoSupermercado;

    public AssinaturaController(AssociarPlano associarPlano, AlterarPlanoSupermercado alterarPlanoSupermercado) {
        this.associarPlano = associarPlano;
        this.alterarPlanoSupermercado = alterarPlanoSupermercado;
    }

    @PostMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Associa um plano comercial a um supermercado")
    public ResponseEntity<AssinaturaResponse> associar(
            @PathVariable Long supermercadoId,
            @Valid @RequestBody AssociarPlanoRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Assinatura assinatura = associarPlano.executar(supermercadoId, request.planoId(), ator.usuarioId(), ator.perfil());
        return ResponseEntity.status(HttpStatus.CREATED).body(AssinaturaResponse.from(assinatura));
    }

    @PatchMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @Operation(summary = "Altera (troca) o plano vigente de um supermercado")
    public ResponseEntity<AssinaturaResponse> alterar(
            @PathVariable Long supermercadoId,
            @Valid @RequestBody AssociarPlanoRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Assinatura assinatura = alterarPlanoSupermercado.executar(supermercadoId, request.planoId(), ator.usuarioId(), ator.perfil());
        return ResponseEntity.ok(AssinaturaResponse.from(assinatura));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
