package com.tabloide.api.modules.autenticacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.application.AutenticarUsuario;
import com.tabloide.api.modules.autenticacao.application.BuscarSessaoPorId;
import com.tabloide.api.modules.autenticacao.application.EncerrarSessao;
import com.tabloide.api.modules.autenticacao.application.ListarSessoes;
import com.tabloide.api.modules.autenticacao.application.SessaoAutenticada;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.JwtTokenService;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.SessaoResumoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping(path = "/api/sessoes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sessão")
public class SessaoController {

    private final AutenticarUsuario autenticarUsuario;
    private final EncerrarSessao encerrarSessao;
    private final JwtTokenService jwtTokenService;
    private final ListarSessoes listarSessoes;
    private final BuscarSessaoPorId buscarSessaoPorId;

    public SessaoController(
            AutenticarUsuario autenticarUsuario,
            EncerrarSessao encerrarSessao,
            JwtTokenService jwtTokenService,
            ListarSessoes listarSessoes,
            BuscarSessaoPorId buscarSessaoPorId
    ) {
        this.autenticarUsuario = autenticarUsuario;
        this.encerrarSessao = encerrarSessao;
        this.jwtTokenService = jwtTokenService;
        this.listarSessoes = listarSessoes;
        this.buscarSessaoPorId = buscarSessaoPorId;
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

    @GetMapping
    @RequerPerfil({Perfil.SUPER_ADMIN, Perfil.DONO})
    @AuditarConsulta(acao = "SESSOES_CONSULTADAS", entidade = "Sessao")
    @Operation(summary = "Lista sessões: Super Admin vê todas, DONO só as do próprio supermercado")
    public ResponseEntity<PaginaResponse<SessaoResumoResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        ClaimsSessao solicitante = contextoObrigatorio();
        Pagina<SessaoDetalhada> resultado = listarSessoes.executar(solicitante.perfil(), solicitante.supermercadoId(), pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, SessaoResumoResponse::from));
    }

    @GetMapping("/{jti}")
    @RequerPerfil({Perfil.SUPER_ADMIN, Perfil.DONO})
    @AuditarConsulta(acao = "SESSAO_CONSULTADA", entidade = "Sessao")
    @Operation(summary = "Consulta uma sessão pelo identificador, respeitando o escopo do perfil")
    public ResponseEntity<SessaoResumoResponse> buscarPorId(@PathVariable String jti) {
        ClaimsSessao solicitante = contextoObrigatorio();
        SessaoDetalhada sessao = buscarSessaoPorId.executar(jti, solicitante.perfil(), solicitante.supermercadoId());
        return ResponseEntity.ok(SessaoResumoResponse.from(sessao));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
