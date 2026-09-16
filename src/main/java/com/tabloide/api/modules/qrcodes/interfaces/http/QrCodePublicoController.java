package com.tabloide.api.modules.qrcodes.interfaces.http;

import com.tabloide.api.modules.qrcodes.application.AcessarQrCode;
import com.tabloide.api.modules.qrcodes.interfaces.http.dto.DestinoQrCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// US-106/US-170/US-188: acesso público (sem autenticação) que resolve o destino estável do QR Code
// e contabiliza o scan, sem depender da campanha vigente na loja.
@RestController
@RequestMapping(path = "/api/publico/qrcodes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "QrCodePublico")
public class QrCodePublicoController {

    private final AcessarQrCode acessarQrCode;

    public QrCodePublicoController(AcessarQrCode acessarQrCode) {
        this.acessarQrCode = acessarQrCode;
    }

    @GetMapping("/{codigoPublico}")
    @Operation(summary = "Resolve o destino público de um QR Code e contabiliza o acesso")
    public ResponseEntity<DestinoQrCodeResponse> acessar(
            @PathVariable String codigoPublico,
            @RequestHeader(value = "Idempotency-Key", required = false) String eventoTecnicoId
    ) {
        return ResponseEntity.ok(DestinoQrCodeResponse.from(acessarQrCode.executar(codigoPublico, eventoTecnicoId)));
    }
}
