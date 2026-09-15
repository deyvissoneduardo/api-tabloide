package com.tabloide.api.modules.conteudopromocional.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.conteudopromocional.application.CadastrarConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.application.DadosConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.application.EditarConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.application.ReordenarConteudosPromocionais;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.interfaces.http.dto.CadastrarConteudoPromocionalRequest;
import com.tabloide.api.modules.conteudopromocional.interfaces.http.dto.ConteudoPromocionalResponse;
import com.tabloide.api.modules.conteudopromocional.interfaces.http.dto.EditarConteudoPromocionalRequest;
import com.tabloide.api.modules.conteudopromocional.interfaces.http.dto.ReordenarConteudosPromocionaisRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping(path = "/api/supermercados/{supermercadoId}/conteudos-promocionais", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "ConteudoPromocional")
public class ConteudoPromocionalController {

    private final CadastrarConteudoPromocional cadastrarConteudoPromocional;
    private final EditarConteudoPromocional editarConteudoPromocional;
    private final ReordenarConteudosPromocionais reordenarConteudosPromocionais;

    public ConteudoPromocionalController(
            CadastrarConteudoPromocional cadastrarConteudoPromocional,
            EditarConteudoPromocional editarConteudoPromocional,
            ReordenarConteudosPromocionais reordenarConteudosPromocionais
    ) {
        this.cadastrarConteudoPromocional = cadastrarConteudoPromocional;
        this.editarConteudoPromocional = editarConteudoPromocional;
        this.reordenarConteudosPromocionais = reordenarConteudosPromocionais;
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Cadastra uma mensagem, aviso ou banner promocional (US-112/113/114)")
    public ResponseEntity<ConteudoPromocionalResponse> cadastrar(
            @PathVariable Long supermercadoId, @Valid @RequestBody CadastrarConteudoPromocionalRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        ConteudoPromocional conteudo = cadastrarConteudoPromocional.executar(
                supermercadoId,
                new DadosConteudoPromocional(
                        request.tipo(), request.titulo(), request.texto(), request.nivel(), request.destino(),
                        request.lojaIds(), request.inicio(), request.fim()
                ),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ConteudoPromocionalResponse.from(conteudo));
    }

    @PatchMapping("/{id}")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Edita título, texto/nível/destino, lojas e validade de um conteúdo promocional (US-115)")
    public ResponseEntity<ConteudoPromocionalResponse> editar(
            @PathVariable Long supermercadoId, @PathVariable Long id, @Valid @RequestBody EditarConteudoPromocionalRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        ConteudoPromocional conteudo = editarConteudoPromocional.executar(
                supermercadoId, id, request.versao(),
                new DadosConteudoPromocional(
                        null, request.titulo(), request.texto(), request.nivel(), request.destino(),
                        request.lojaIds(), request.inicio(), request.fim()
                ),
                ator.usuarioId(), ator.perfil(), ator.supermercadoId()
        );
        return ResponseEntity.ok(ConteudoPromocionalResponse.from(conteudo));
    }

    @PatchMapping("/reordenacao")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Reordena todos os conteúdos promocionais do supermercado (US-116)")
    public ResponseEntity<List<ConteudoPromocionalResponse>> reordenar(
            @PathVariable Long supermercadoId, @Valid @RequestBody ReordenarConteudosPromocionaisRequest request
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        List<ConteudoPromocional> reordenados = reordenarConteudosPromocionais.executar(
                supermercadoId, request.idsEmOrdem(), ator.usuarioId(), ator.perfil(), ator.supermercadoId()
        );
        return ResponseEntity.ok(reordenados.stream().map(ConteudoPromocionalResponse::from).toList());
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
