package com.tabloide.api.modules.autenticacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.application.RedefinirSenha;
import com.tabloide.api.modules.autenticacao.application.ResultadoVerificacaoRedefinicao;
import com.tabloide.api.modules.autenticacao.application.VerificarESolicitarRedefinicaoSenha;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.RedefinirSenhaRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.VerificarRedefinicaoSenhaRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.VerificarRedefinicaoSenhaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/redefinicoes-senha", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Redefinição de senha")
public class RedefinicaoSenhaController {

    private final VerificarESolicitarRedefinicaoSenha verificarESolicitarRedefinicaoSenha;
    private final RedefinirSenha redefinirSenha;

    public RedefinicaoSenhaController(
            VerificarESolicitarRedefinicaoSenha verificarESolicitarRedefinicaoSenha,
            RedefinirSenha redefinirSenha
    ) {
        this.verificarESolicitarRedefinicaoSenha = verificarESolicitarRedefinicaoSenha;
        this.redefinirSenha = redefinirSenha;
    }

    @PostMapping
    @Operation(summary = "Verifica CNPJ e e-mail cadastrados e emite um token de redefinicao de senha")
    public ResponseEntity<VerificarRedefinicaoSenhaResponse> verificar(@Valid @RequestBody VerificarRedefinicaoSenhaRequest request) {
        ResultadoVerificacaoRedefinicao resultado = verificarESolicitarRedefinicaoSenha.verificar(request.cnpj(), request.email());
        return ResponseEntity.ok(new VerificarRedefinicaoSenhaResponse(resultado.token(), resultado.expiraEm()));
    }

    @PutMapping("/{token}")
    @Operation(summary = "Cadastra uma nova senha usando o token emitido na verificacao")
    public ResponseEntity<Void> redefinir(@PathVariable String token, @Valid @RequestBody RedefinirSenhaRequest request) {
        redefinirSenha.redefinir(token, request.novaSenha());
        return ResponseEntity.noContent().build();
    }
}
