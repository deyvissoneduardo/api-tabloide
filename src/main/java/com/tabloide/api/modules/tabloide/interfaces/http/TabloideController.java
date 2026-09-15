package com.tabloide.api.modules.tabloide.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.tabloide.application.BuscarTabloideAtualPorSupermercado;
import com.tabloide.api.modules.tabloide.application.DadosTabloide;
import com.tabloide.api.modules.tabloide.application.DisponibilizarTabloide;
import com.tabloide.api.modules.tabloide.domain.Tabloide;
import com.tabloide.api.modules.tabloide.interfaces.http.dto.DisponibilizarTabloideRequest;
import com.tabloide.api.modules.tabloide.interfaces.http.dto.TabloideResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/supermercados/{supermercadoId}/tabloides", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tabloide")
public class TabloideController {

    private final DisponibilizarTabloide disponibilizarTabloide;
    private final BuscarTabloideAtualPorSupermercado buscarTabloideAtualPorSupermercado;

    public TabloideController(
            DisponibilizarTabloide disponibilizarTabloide,
            BuscarTabloideAtualPorSupermercado buscarTabloideAtualPorSupermercado
    ) {
        this.disponibilizarTabloide = disponibilizarTabloide;
        this.buscarTabloideAtualPorSupermercado = buscarTabloideAtualPorSupermercado;
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Disponibiliza um tabloide já existente (arquivo, validade e lojas)")
    public ResponseEntity<TabloideResponse> disponibilizar(
            @PathVariable Long supermercadoId, @Valid @RequestBody DisponibilizarTabloideRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Tabloide tabloide = disponibilizarTabloide.executar(
                supermercadoId,
                new DadosTabloide(
                        request.titulo(), request.tipoArquivo(), request.arquivoPdfUrl(), request.arquivoPdfTamanhoBytes(),
                        request.lojaIds(), request.inicio(), request.fim()
                ),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TabloideResponse.from(tabloide));
    }

    @GetMapping("/atual")
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR})
    @AuditarConsulta(acao = "TABLOIDE_ATUAL_CONSULTADO", entidade = "Tabloide")
    @Operation(summary = "Consulta o tabloide vigente do supermercado; 204 quando não há nenhum vigente")
    public ResponseEntity<TabloideResponse> buscarAtual(@PathVariable Long supermercadoId) {
        ClaimsSessao ator = contextoObrigatorio();
        Optional<Tabloide> tabloide = buscarTabloideAtualPorSupermercado.executar(supermercadoId, ator.supermercadoId());
        return tabloide
                .map(t -> ResponseEntity.ok(TabloideResponse.from(t)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
