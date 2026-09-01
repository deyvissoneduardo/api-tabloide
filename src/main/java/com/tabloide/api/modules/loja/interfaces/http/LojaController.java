package com.tabloide.api.modules.loja.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.infrastructure.security.AuditarConsulta;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ClaimsSessao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.ContextoAutenticacao;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RequerPerfil;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.PaginaResponse;
import com.tabloide.api.modules.loja.application.AtivarLoja;
import com.tabloide.api.modules.loja.application.BuscarLojaPorId;
import com.tabloide.api.modules.loja.application.CadastrarLoja;
import com.tabloide.api.modules.loja.application.DadosLoja;
import com.tabloide.api.modules.loja.application.DesativarLoja;
import com.tabloide.api.modules.loja.application.ListarLojasPorSupermercado;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.interfaces.http.dto.CadastrarLojaRequest;
import com.tabloide.api.modules.loja.interfaces.http.dto.LojaResponse;
import com.tabloide.api.modules.supermercado.domain.Endereco;
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
@RequestMapping(path = "/api/supermercados/{supermercadoId}/lojas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Loja")
public class LojaController {

    private final CadastrarLoja cadastrarLoja;
    private final ListarLojasPorSupermercado listarLojasPorSupermercado;
    private final BuscarLojaPorId buscarLojaPorId;
    private final AtivarLoja ativarLoja;
    private final DesativarLoja desativarLoja;

    public LojaController(
            CadastrarLoja cadastrarLoja,
            ListarLojasPorSupermercado listarLojasPorSupermercado,
            BuscarLojaPorId buscarLojaPorId,
            AtivarLoja ativarLoja,
            DesativarLoja desativarLoja
    ) {
        this.cadastrarLoja = cadastrarLoja;
        this.listarLojasPorSupermercado = listarLojasPorSupermercado;
        this.buscarLojaPorId = buscarLojaPorId;
        this.ativarLoja = ativarLoja;
        this.desativarLoja = desativarLoja;
    }

    @GetMapping
    @RequerPerfil({Perfil.DONO, Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "LOJAS_CONSULTADAS", entidade = "Loja", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Lista as lojas de um supermercado, paginado")
    public ResponseEntity<PaginaResponse<LojaResponse>> listar(
            @PathVariable Long supermercadoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "25") int tamanho
    ) {
        ClaimsSessao ator = contextoObrigatorio();
        Pagina<Loja> resultado = listarLojasPorSupermercado.executar(supermercadoId, ator.perfil(), ator.supermercadoId(), pagina, tamanho);
        return ResponseEntity.ok(PaginaResponse.from(resultado, LojaResponse::from));
    }

    @GetMapping("/{id}")
    @RequerPerfil({Perfil.DONO, Perfil.SUPER_ADMIN})
    @AuditarConsulta(acao = "LOJA_CONSULTADA", entidade = "Loja", paramEntidadeId = "id", paramSupermercadoId = "supermercadoId")
    @Operation(summary = "Consulta os dados cadastrais e o estado de uma loja")
    public ResponseEntity<LojaResponse> buscarPorId(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Loja loja = buscarLojaPorId.executar(supermercadoId, id, ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(LojaResponse.from(loja));
    }

    @PostMapping
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Cadastra uma loja no supermercado, com seu endereço")
    public ResponseEntity<LojaResponse> cadastrar(@PathVariable Long supermercadoId, @Valid @RequestBody CadastrarLojaRequest request) {
        ClaimsSessao ator = contextoObrigatorio();
        Loja loja = cadastrarLoja.executar(
                supermercadoId,
                new DadosLoja(
                        request.nome(),
                        new Endereco(request.cep(), request.logradouro(), request.numero(), request.bairro(), request.municipio(), request.uf()),
                        request.complemento()
                ),
                ator.usuarioId(),
                ator.perfil(),
                ator.supermercadoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(LojaResponse.from(loja));
    }

    @PostMapping("/{id}/ativacao")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Ativa (reativa) uma loja desativada")
    public ResponseEntity<LojaResponse> ativar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Loja loja = ativarLoja.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(LojaResponse.from(loja));
    }

    @PostMapping("/{id}/desativacao")
    @RequerPerfil({Perfil.DONO})
    @Operation(summary = "Desativa uma loja ativa, tornando página e QR Codes indisponíveis")
    public ResponseEntity<LojaResponse> desativar(@PathVariable Long supermercadoId, @PathVariable Long id) {
        ClaimsSessao ator = contextoObrigatorio();
        Loja loja = desativarLoja.executar(supermercadoId, id, ator.usuarioId(), ator.perfil(), ator.supermercadoId());
        return ResponseEntity.ok(LojaResponse.from(loja));
    }

    private ClaimsSessao contextoObrigatorio() {
        return ContextoAutenticacao.atual().orElseThrow(SessaoInvalidaOuExpiradaException::new);
    }
}
