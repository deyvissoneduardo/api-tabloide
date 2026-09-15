package com.tabloide.api.modules.oferta.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.categoria.interfaces.http.dto.CadastrarCategoriaRequest;
import com.tabloide.api.modules.loja.interfaces.http.dto.CadastrarLojaRequest;
import com.tabloide.api.modules.oferta.interfaces.http.dto.CadastrarOfertaRequest;
import com.tabloide.api.modules.oferta.interfaces.http.dto.EditarOfertaRequest;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaRepository;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaRepository;
import com.tabloide.api.modules.produto.interfaces.http.dto.CadastrarProdutoRequest;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaEntity;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class OfertaControllerIT extends OfertaIntegrationTestSupport {

    private static final String SENHA_PADRAO = "SenhaCorreta1";

    @Autowired
    private SupermercadoJpaRepository supermercadoJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PlanoJpaRepository planoJpaRepository;

    @Autowired
    private AssinaturaJpaRepository assinaturaJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCadastrarOfertaAssociadaAUmaOuMaisLojas() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777005040");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-cadastra-oferta@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);
        long lojaUm = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        long lojaDois = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Dois");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestOferta(produtoId, Set.of(lojaUm, lojaDois))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RASCUNHO"))
                .andExpect(jsonPath("$.percentualDesconto").value(25))
                .andExpect(jsonPath("$.lojaIds.length()").value(2));
    }

    @Test
    void deveRejeitarCadastroComOperadorDeOutroSupermercado() throws Exception {
        Long supermercadoA = criarSupermercadoComPlano("11444777005120");
        Long supermercadoB = criarSupermercadoComPlano("11444777005201");
        String tokenDonoA = criarDonoEAutenticar(supermercadoA, "dono-a-oferta@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoA, tokenDonoA, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoA, tokenDonoA, categoriaId);
        long lojaId = cadastrarLojaECapturarId(supermercadoA, tokenDonoA, "Loja Um");
        String tokenDonoB = criarDonoEAutenticar(supermercadoB, "dono-b-oferta@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoA, tokenDonoB, requestOferta(produtoId, Set.of(lojaId))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarCadastroSemLojas() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777005392");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-oferta-sem-loja@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestOferta(produtoId, Set.of())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarCadastroComPrecoPromocionalMaiorQueONormal() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777005473");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-oferta-preco-invalido@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, new CadastrarOfertaRequest(
                        produtoId, Set.of(lojaId), new BigDecimal("10.00"), new BigDecimal("15.00"),
                        agora, agora.plus(1, ChronoUnit.DAYS), null, false)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveFicarVigenteQuandoPublicadaDentroDoPeriodo() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777005554");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-oferta-vigente@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, new CadastrarOfertaRequest(
                        produtoId, Set.of(lojaId), new BigDecimal("10.00"), new BigDecimal("7.50"),
                        agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), "Válido enquanto durar o estoque", true)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("VIGENTE"));
    }

    @Test
    void deveEditarOfertaAtualizandoCampos() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006011");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-oferta-editar@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        tools.jackson.databind.JsonNode ofertaCriada = cadastrarOfertaECapturarCorpo(supermercadoId, tokenDono, requestOferta(produtoId, Set.of(lojaId)));
        Instant agora = Instant.now();

        mockMvc.perform(editar(supermercadoId, tokenDono, ofertaCriada.get("id").asLong(), new EditarOfertaRequest(
                        ofertaCriada.get("versao").asLong(), produtoId, Set.of(lojaId),
                        new BigDecimal("20.00"), new BigDecimal("10.00"), agora, agora.plus(3, ChronoUnit.DAYS), "Condição editada")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precoNormal").value(20.00))
                .andExpect(jsonPath("$.condicoes").value("Condição editada"));
    }

    @Test
    void deveRejeitarEdicaoComVersaoDesatualizada() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006100");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-oferta-versao@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        tools.jackson.databind.JsonNode ofertaCriada = cadastrarOfertaECapturarCorpo(supermercadoId, tokenDono, requestOferta(produtoId, Set.of(lojaId)));
        Instant agora = Instant.now();

        mockMvc.perform(editar(supermercadoId, tokenDono, ofertaCriada.get("id").asLong(), new EditarOfertaRequest(
                        999L, produtoId, Set.of(lojaId), new BigDecimal("20.00"), new BigDecimal("10.00"),
                        agora, agora.plus(3, ChronoUnit.DAYS), null)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveCancelarOferta() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006283");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-oferta-cancelar@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        tools.jackson.databind.JsonNode ofertaCriada = cadastrarOfertaECapturarCorpo(supermercadoId, tokenDono, requestOferta(produtoId, Set.of(lojaId)));

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/ofertas/" + ofertaCriada.get("id").asLong() + "/cancelamento")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void deveDesativarEReativarOfertaRespeitandoVigencia() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006364");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-oferta-ativar@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();
        tools.jackson.databind.JsonNode ofertaCriada = cadastrarOfertaECapturarCorpo(supermercadoId, tokenDono, new CadastrarOfertaRequest(
                produtoId, Set.of(lojaId), new BigDecimal("10.00"), new BigDecimal("7.50"),
                agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), null, true));
        long ofertaId = ofertaCriada.get("id").asLong();

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/ofertas/" + ofertaId + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DESATIVADA"));

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/ofertas/" + ofertaId + "/ativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("VIGENTE"));
    }

    @Test
    void deveCopiarOfertaComoNovoRascunho() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006445");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-oferta-copiar@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, tokenDono, categoriaId);
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();
        tools.jackson.databind.JsonNode ofertaCriada = cadastrarOfertaECapturarCorpo(supermercadoId, tokenDono, new CadastrarOfertaRequest(
                produtoId, Set.of(lojaId), new BigDecimal("10.00"), new BigDecimal("7.50"),
                agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), "Condição original", true));
        long ofertaId = ofertaCriada.get("id").asLong();

        String corpoCopia = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/ofertas/" + ofertaId + "/copia")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RASCUNHO"))
                .andExpect(jsonPath("$.condicoes").value("Condição original"))
                .andReturn().getResponse().getContentAsString();

        long copiaId = objectMapper.readTree(corpoCopia).get("id").asLong();
        org.assertj.core.api.Assertions.assertThat(copiaId).isNotEqualTo(ofertaId);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder editar(
            Long supermercadoId, String token, Long ofertaId, EditarOfertaRequest request) throws Exception {
        return patch("/api/supermercados/" + supermercadoId + "/ofertas/" + ofertaId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private tools.jackson.databind.JsonNode cadastrarOfertaECapturarCorpo(Long supermercadoId, String token, CadastrarOfertaRequest request) throws Exception {
        String corpo = mockMvc.perform(cadastrar(supermercadoId, token, request))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder cadastrar(
            Long supermercadoId, String token, CadastrarOfertaRequest request) throws Exception {
        return post("/api/supermercados/" + supermercadoId + "/ofertas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private CadastrarOfertaRequest requestOferta(long produtoId, Set<Long> lojaIds) {
        Instant agora = Instant.now();
        return new CadastrarOfertaRequest(
                produtoId, lojaIds, new BigDecimal("10.00"), new BigDecimal("7.50"),
                agora, agora.plus(1, ChronoUnit.DAYS), "Enquanto durar o estoque", false
        );
    }

    private long cadastrarCategoriaECapturarId(Long supermercadoId, String token, String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/categorias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CadastrarCategoriaRequest(nome, null))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private long cadastrarProdutoECapturarId(Long supermercadoId, String token, long categoriaId) throws Exception {
        String corpo = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/produtos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CadastrarProdutoRequest(
                                "Refrigerante Cola 2L", Set.of(categoriaId), null, null, null, null, null))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private long cadastrarLojaECapturarId(Long supermercadoId, String token, String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/lojas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CadastrarLojaRequest(nome, "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP", null))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private Long criarSupermercadoComPlano(String cnpj) {
        Instant agora = Instant.now();
        SupermercadoJpaEntity supermercado = supermercadoJpaRepository.save(new SupermercadoJpaEntity(
                null, cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP",
                null, null, null, EstadoSupermercado.ATIVO, null, agora, agora
        ));

        PlanoJpaEntity plano = planoJpaRepository.save(new PlanoJpaEntity(
                null, "Plano " + cnpj, "plano " + cnpj, 30, BigDecimal.TEN, 100, null, null, agora, agora, null
        ));

        assinaturaJpaRepository.save(new AssinaturaJpaEntity(
                null, supermercado.getId(), plano.getId(), plano.getNome(), plano.getValidadeDias(), plano.getValor(),
                plano.getLimiteFotos(), EstadoAssinatura.VIGENTE, agora, agora.plusSeconds(3600 * 24 * 30), agora
        ));

        return supermercado.getId();
    }

    private String criarDonoEAutenticar(Long supermercadoId, String email) throws Exception {
        return criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, email);
    }

    private String criarUsuarioEAutenticar(Perfil perfil, Long supermercadoId, String email) throws Exception {
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode(SENHA_PADRAO), perfil, supermercadoId, null, true, 0, null, agora, agora
        ));
        return autenticarEExtrairToken(email, SENHA_PADRAO);
    }

    private String autenticarEExtrairToken(String email, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, senha))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(corpo, LoginResponse.class).token();
    }
}
