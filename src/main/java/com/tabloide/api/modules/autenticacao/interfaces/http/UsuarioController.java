package com.tabloide.api.modules.autenticacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.application.ListarUsuariosPorSupermercado;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/supermercados/{supermercadoId}/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Usuário")
public class UsuarioController {

    private final ListarUsuariosPorSupermercado listarUsuariosPorSupermercado;

    public UsuarioController(ListarUsuariosPorSupermercado listarUsuariosPorSupermercado) {
        this.listarUsuariosPorSupermercado = listarUsuariosPorSupermercado;
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
}
