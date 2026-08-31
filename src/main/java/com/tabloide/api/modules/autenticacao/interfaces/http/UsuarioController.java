package com.tabloide.api.modules.autenticacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.application.AtivarUsuario;
import com.tabloide.api.modules.autenticacao.application.CadastrarUsuarioAdministrativo;
import com.tabloide.api.modules.autenticacao.application.DadosNovoUsuarioAdministrativo;
import com.tabloide.api.modules.autenticacao.application.DesativarUsuario;
import com.tabloide.api.modules.autenticacao.application.ListarUsuariosPorSupermercado;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.CadastrarUsuarioAdministrativoRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/supermercados/{supermercadoId}/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Usuário")
public class UsuarioController {

    private final ListarUsuariosPorSupermercado listarUsuariosPorSupermercado;
    private final CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo;
    private final AtivarUsuario ativarUsuario;
    private final DesativarUsuario desativarUsuario;

    public UsuarioController(
            ListarUsuariosPorSupermercado listarUsuariosPorSupermercado,
            CadastrarUsuarioAdministrativo cadastrarUsuarioAdministrativo,
            AtivarUsuario ativarUsuario,
            DesativarUsuario desativarUsuario
    ) {
        this.listarUsuariosPorSupermercado = listarUsuariosPorSupermercado;
        this.cadastrarUsuarioAdministrativo = cadastrarUsuarioAdministrativo;
        this.ativarUsuario = ativarUsuario;
        this.desativarUsuario = desativarUsuario;
    }

    @GetMapping
    @RequerPerfil({Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "USUARIOS_SUPERMERCADO_CONSULTADOS", entidade = "Usuario", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Lista os usuários de um supermercado")
    public ResponseEntity<PaginaResponse<UsuarioResponse>> listar(
            @PathVariable Long supermercadoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        Pagina<Usuario> resultado = listarUsuariosPorSupermercado.executar(supermercadoId, pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, UsuarioResponse::from));
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Cadastra um usuário administrativo (DONO ou OPERADOR) no supermercado")
    public ResponseEntity<UsuarioResponse> cadastrar(
            @PathVariable Long supermercadoId,
            @Valid @RequestBody CadastrarUsuarioAdministrativoRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Usuario usuario = cadastrarUsuarioAdministrativo.executar(
                supermercadoId,
                new DadosNovoUsuarioAdministrativo(request.email(), request.senha(), request.perfil()),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(usuario));
    }

    @PostMapping("/{usuarioId}/ativacao")
    @RequerPerfil({Perfil.DONO, Perfil.SUPER_ADMIN})
    @Operation(summary = "Ativa um usuário do supermercado")
    public ResponseEntity<UsuarioResponse> ativar(@PathVariable Long supermercadoId, @PathVariable Long usuarioId) {
        ClaimsSessao ator = contextoObrigatorio();
        Usuario usuario = ativarUsuario.executar(supermercadoId, usuarioId, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @PostMapping("/{usuarioId}/desativacao")
    @RequerPerfil({Perfil.DONO, Perfil.SUPER_ADMIN})
    @Operation(summary = "Desativa um usuário do supermercado, encerrando suas sessões ativas")
    public ResponseEntity<UsuarioResponse> desativar(@PathVariable Long supermercadoId, @PathVariable Long usuarioId) {
        ClaimsSessao ator = contextoObrigatorio();
        Usuario usuario = desativarUsuario.executar(supermercadoId, usuarioId, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
