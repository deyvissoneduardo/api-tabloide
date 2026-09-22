package com.tabloide.api.modules.campanha.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.campanha.interfaces.http.dto.CadastrarCampanhaRequest;
import com.tabloide.api.modules.campanha.interfaces.http.dto.EditarCampanhaRequest;
import com.tabloide.api.modules.categoria.interfaces.http.dto.CadastrarCategoriaRequest;
import com.tabloide.api.modules.loja.interfaces.http.dto.CadastrarLojaRequest;
import com.tabloide.api.modules.oferta.interfaces.http.dto.CadastrarOfertaRequest;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class CampanhaControllerIT extends CampanhaIntegrationTestSupport {

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
    void deveCadastrarCampanhaComLojaSemOfertas() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006011");
        String token = criarDonoEAutenticar(supermercadoId, "dono-cadastra-campanha@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Um");

        mockMvc.perform(cadastrar(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RASCUNHO"))
                .andExpect(jsonPath("$.lojaIds.length()").value(1));
    }

    @Test
    void deveCadastrarCampanhaComOfertaVinculada() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006100");
        String token = criarDonoEAutenticar(supermercadoId, "dono-campanha-oferta@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Um");
        long ofertaId = cadastrarOfertaECapturarId(supermercadoId, token, lojaId);

        mockMvc.perform(cadastrar(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of(ofertaId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ofertaIds.length()").value(1));
    }

    @Test
    void deveRejeitarQuandoOfertaJaVinculadaAOutraCampanhaAtiva() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006283");
        String token = criarDonoEAutenticar(supermercadoId, "dono-campanha-oferta-dup@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Um");
        long ofertaId = cadastrarOfertaECapturarId(supermercadoId, token, lojaId);

        mockMvc.perform(cadastrar(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of(ofertaId))))
                .andExpect(status().isCreated());

        mockMvc.perform(cadastrar(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of(ofertaId))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCadastroComOperador() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006364");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-campanha-operador@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        String tokenOperador = criarOperadorEAutenticar(supermercadoId, "operador-campanha@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenOperador, requestCampanha(Set.of(lojaId), Set.of())))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveEditarCampanhaNaoFinalizada() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006445");
        String token = criarDonoEAutenticar(supermercadoId, "dono-edita-campanha@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Um");
        Long campanhaId = cadastrarCampanhaECapturarId(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of()));

        Instant agora = Instant.now();
        mockMvc.perform(patch("/api/supermercados/" + supermercadoId + "/campanhas/" + campanhaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EditarCampanhaRequest(
                                0L, "Campanha Editada", "Nova descrição", Set.of(lojaId), Set.of(), agora, agora.plus(2, ChronoUnit.DAYS)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Campanha Editada"))
                .andExpect(jsonPath("$.versao").value(1));
    }

    @Test
    void deveCancelarCampanhaESerIdempotente() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006526");
        String token = criarDonoEAutenticar(supermercadoId, "dono-cancela-campanha@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Um");
        Long campanhaId = cadastrarCampanhaECapturarId(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of()));

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/campanhas/" + campanhaId + "/cancelamento")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/campanhas/" + campanhaId + "/cancelamento")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void deveCompararCampanhasDoMesmoSupermercado() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006607");
        String token = criarDonoEAutenticar(supermercadoId, "dono-compara-campanha@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Um");
        Long campanhaUm = cadastrarCampanhaECapturarId(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of()));
        Long campanhaDois = cadastrarCampanhaECapturarId(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of()));

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/campanhas/comparacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("ids", String.valueOf(campanhaUm), String.valueOf(campanhaDois)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveReaproveitarOfertaDeCampanhaAnteriorAtravesDeCopia() throws Exception {
        // US-187: reaproveitar campanha anterior = copiar suas ofertas (RN-004) e vincular a uma nova
        // campanha; não existe (nem é exigido pela decisão consolidada) um endpoint de "copiar campanha".
        Long supermercadoId = criarSupermercadoComPlano("11444777006879");
        String token = criarDonoEAutenticar(supermercadoId, "dono-reaproveita-campanha@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Um");
        long ofertaOriginalId = cadastrarOfertaECapturarId(supermercadoId, token, lojaId);
        Long campanhaAnterior = cadastrarCampanhaECapturarId(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of(ofertaOriginalId)));

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/campanhas/" + campanhaAnterior + "/cancelamento")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        String corpoCopia = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/ofertas/" + ofertaOriginalId + "/copia")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long ofertaCopiadaId = objectMapper.readTree(corpoCopia).get("id").asLong();

        mockMvc.perform(cadastrar(supermercadoId, token, requestCampanha(Set.of(lojaId), Set.of(ofertaCopiadaId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ofertaIds[0]").value(ofertaCopiadaId));
    }

    @Test
    void deveRejeitarCadastroSemLojas() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777006798");
        String token = criarDonoEAutenticar(supermercadoId, "dono-campanha-sem-loja@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, token, requestCampanha(Set.of(), Set.of())))
                .andExpect(status().isUnprocessableContent());
    }

    private MockHttpServletRequestBuilder cadastrar(Long supermercadoId, String token, CadastrarCampanhaRequest request) throws Exception {
        return post("/api/supermercados/" + supermercadoId + "/campanhas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private Long cadastrarCampanhaECapturarId(Long supermercadoId, String token, CadastrarCampanhaRequest request) throws Exception {
        String corpo = mockMvc.perform(cadastrar(supermercadoId, token, request))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private CadastrarCampanhaRequest requestCampanha(Set<Long> lojaIds, Set<Long> ofertaIds) {
        Instant agora = Instant.now();
        return new CadastrarCampanhaRequest("Campanha", "Descrição", lojaIds, ofertaIds, agora, agora.plus(1, ChronoUnit.DAYS), false);
    }

    private long cadastrarOfertaECapturarId(Long supermercadoId, String token, long lojaId) throws Exception {
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, token, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, token, categoriaId);
        Instant agora = Instant.now();
        String corpo = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/ofertas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CadastrarOfertaRequest(
                                produtoId, Set.of(lojaId), new BigDecimal("10.00"), new BigDecimal("7.50"),
                                agora, agora.plus(1, ChronoUnit.DAYS), null, false))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
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
        return criarUsuarioEAutenticar(com.tabloide.api.modules.autenticacao.domain.Perfil.DONO, supermercadoId, email);
    }

    private String criarOperadorEAutenticar(Long supermercadoId, String email) throws Exception {
        return criarUsuarioEAutenticar(com.tabloide.api.modules.autenticacao.domain.Perfil.OPERADOR, supermercadoId, email);
    }

    private String criarUsuarioEAutenticar(com.tabloide.api.modules.autenticacao.domain.Perfil perfil, Long supermercadoId, String email) throws Exception {
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
