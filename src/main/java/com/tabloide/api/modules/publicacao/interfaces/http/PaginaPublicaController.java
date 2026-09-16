package com.tabloide.api.modules.publicacao.interfaces.http;

import com.tabloide.api.modules.publicacao.application.BuscarOfertaPublicaPorId;
import com.tabloide.api.modules.publicacao.application.ConsultarLojaPublica;
import com.tabloide.api.modules.publicacao.application.ListarOfertasVigentesDaLoja;
import com.tabloide.api.modules.publicacao.interfaces.http.dto.LojaPublicaResponse;
import com.tabloide.api.modules.publicacao.interfaces.http.dto.OfertaPublicaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// US-171/US-179: catálogo público, sem autenticação e sem coleta de dados pessoais.
@RestController
@RequestMapping(path = "/api/publico/lojas/{lojaId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "PaginaPublica")
public class PaginaPublicaController {

    private final ConsultarLojaPublica consultarLojaPublica;
    private final ListarOfertasVigentesDaLoja listarOfertasVigentesDaLoja;
    private final BuscarOfertaPublicaPorId buscarOfertaPublicaPorId;

    public PaginaPublicaController(
            ConsultarLojaPublica consultarLojaPublica,
            ListarOfertasVigentesDaLoja listarOfertasVigentesDaLoja,
            BuscarOfertaPublicaPorId buscarOfertaPublicaPorId
    ) {
        this.consultarLojaPublica = consultarLojaPublica;
        this.listarOfertasVigentesDaLoja = listarOfertasVigentesDaLoja;
        this.buscarOfertaPublicaPorId = buscarOfertaPublicaPorId;
    }

    @GetMapping
    @Operation(summary = "Identidade pública do supermercado e da loja")
    public ResponseEntity<LojaPublicaResponse> consultarLoja(@PathVariable Long lojaId) {
        return ResponseEntity.ok(LojaPublicaResponse.from(consultarLojaPublica.executar(lojaId)));
    }

    @GetMapping("/ofertas")
    @Operation(summary = "Lista as ofertas vigentes da loja, opcionalmente filtradas por categoria")
    public ResponseEntity<List<OfertaPublicaResponse>> listarOfertas(
            @PathVariable Long lojaId, @RequestParam(required = false) Long categoriaId
    ) {
        List<OfertaPublicaResponse> ofertas = listarOfertasVigentesDaLoja.executar(lojaId, categoriaId).stream()
                .map(OfertaPublicaResponse::from)
                .toList();
        return ResponseEntity.ok(ofertas);
    }

    @GetMapping("/ofertas/{ofertaId}")
    @Operation(summary = "Detalhe público de uma oferta vigente da loja")
    public ResponseEntity<OfertaPublicaResponse> buscarOferta(@PathVariable Long lojaId, @PathVariable Long ofertaId) {
        return ResponseEntity.ok(OfertaPublicaResponse.from(buscarOfertaPublicaPorId.executar(lojaId, ofertaId)));
    }
}
