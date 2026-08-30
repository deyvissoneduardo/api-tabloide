package com.tabloide.api.modules.autenticacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.application.AutenticarUsuario;
import com.tabloide.api.modules.autenticacao.application.EncerrarSessao;
import com.tabloide.api.modules.autenticacao.application.SessaoAutenticada;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.JwtTokenService;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/sessoes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sessão")
public class SessaoController {

    private final AutenticarUsuario autenticarUsuario;
    private final EncerrarSessao encerrarSessao;
    private final JwtTokenService jwtTokenService;

    public SessaoController(
            AutenticarUsuario autenticarUsuario,
            EncerrarSessao encerrarSessao,
            JwtTokenService jwtTokenService
    ) {
        this.autenticarUsuario = autenticarUsuario;
        this.encerrarSessao = encerrarSessao;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    @Operation(summary = "Autentica um usuario por e-mail e senha e cria uma sessao")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        SessaoAutenticada resultado = autenticarUsuario.autenticar(request.email(), request.senha());
        String token = jwtTokenService.emitir(resultado.usuario(), resultado.sessao());
        return ResponseEntity.ok(new LoginResponse(token, resultado.usuario().perfil(), resultado.sessao().expiraEm()));
    }

    @DeleteMapping
    @Operation(summary = "Encerra a sessao atual")
    public ResponseEntity<Void> logout() {
        ContextoAutenticacao.atual().ifPresent(claims -> encerrarSessao.encerrar(claims.jti()));
        return ResponseEntity.noContent().build();
    }
}
