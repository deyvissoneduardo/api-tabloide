package com.tabloide.api.modules.autenticacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.application.AlterarPropriosDados;
import com.tabloide.api.modules.autenticacao.application.DadosAlteracaoPropriosDados;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.AlterarPropriosDadosRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/usuarios/me", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Usuário")
public class MinhaContaController {

    private final AlterarPropriosDados alterarPropriosDados;

    public MinhaContaController(AlterarPropriosDados alterarPropriosDados) {
        this.alterarPropriosDados = alterarPropriosDados;
    }

    @PatchMapping
    @RequerPerfil({Perfil.DONO, Perfil.OPERADOR})
    @Operation(summary = "Altera o e-mail e/ou a senha da própria conta; trocar a senha encerra todas as sessões")
    public ResponseEntity<UsuarioResponse> alterarMeusDados(@Valid @RequestBody AlterarPropriosDadosRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Usuario usuario = alterarPropriosDados.executar(
                ator.usuarioId(),
                new DadosAlteracaoPropriosDados(request.senhaAtual(), request.novoEmail(), request.novaSenha()),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
