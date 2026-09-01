package com.tabloide.api.modules.loja.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.loja.interfaces.http.dto.CadastrarLojaRequest;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaRepository;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaRepository;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaEntity;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class LojaControllerIT extends LojaIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";
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
    void deveCadastrarLojaComoDono() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777001303", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-cadastra-loja@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestLoja("Loja Centro")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Loja Centro"))
                .andExpect(jsonPath("$.estado").value("ATIVA"));
    }

    @Test
    void deveRejeitarCadastroSemToken() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777001486", null);

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/lojas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLoja("Loja Centro"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarCadastroComOperador() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777001567", null);
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-nao-cadastra-loja@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenOperador, requestLoja("Loja Centro")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarCadastroComDonoDeOutroSupermercado() throws Exception {
        Long supermercadoA = criarSupermercadoComPlano("11444777001648", null);
        Long supermercadoB = criarSupermercadoComPlano("11444777001729", null);
        String tokenDonoA = criarDonoEAutenticar(supermercadoA, "dono-fora-escopo-loja@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoB, tokenDonoA, requestLoja("Loja Centro")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarNomeDuplicadoNoMesmoSupermercado() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777001800", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-nome-duplicado@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestLoja("Loja Centro")))
                .andExpect(status().isCreated());
        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestLoja("  loja CENTRO  ")))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCadastroQuandoSupermercadoDesativado() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777001990", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-supermercado-desativado@sgtm.local");
        SupermercadoJpaEntity supermercado = supermercadoJpaRepository.findById(supermercadoId).orElseThrow();
        supermercado.setEstado(EstadoSupermercado.DESATIVADO);
        supermercadoJpaRepository.save(supermercado);

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestLoja("Loja Centro")))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCadastroQuandoLimiteDoPlanoAtingido() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002024", 1);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-limite-plano@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestLoja("Loja Um")))
                .andExpect(status().isCreated());

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestLoja("Loja Dois")))
                .andExpect(status().isConflict());
    }

    @Test
    void deveAtivarELojaAposDesativar() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002105", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-ativa-desativa-loja@sgtm.local");
        long lojaId = cadastrarECapturarId(supermercadoId, tokenDono, "Loja Centro");

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/lojas/" + lojaId + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DESATIVADA"));

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/lojas/" + lojaId + "/ativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATIVA"));
    }

    @Test
    void deveRejeitarAtivacaoComOperador() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002296", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-cria-para-operador@sgtm.local");
        long lojaId = cadastrarECapturarId(supermercadoId, tokenDono, "Loja Centro");
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-nao-ativa-loja@sgtm.local");

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/lojas/" + lojaId + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveListarLojasDoSupermercadoComoDono() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002377", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-lista-lojas@sgtm.local");
        cadastrarECapturarId(supermercadoId, tokenDono, "Loja Um");
        cadastrarECapturarId(supermercadoId, tokenDono, "Loja Dois");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/lojas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(2));
    }

    @Test
    void deveBuscarLojaPorIdComoSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002458", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-busca-loja@sgtm.local");
        long lojaId = cadastrarECapturarId(supermercadoId, tokenDono, "Loja Centro");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/lojas/" + lojaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lojaId));
    }

    @Test
    void deveRetornarNaoEncontradaParaLojaInexistente() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002539", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-loja-inexistente@sgtm.local");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/lojas/999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isNotFound());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder cadastrar(
            Long supermercadoId, String token, CadastrarLojaRequest request) throws Exception {
        return post("/api/supermercados/" + supermercadoId + "/lojas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private long cadastrarECapturarId(Long supermercadoId, String token, String nome) throws Exception {
        String corpo = mockMvc.perform(cadastrar(supermercadoId, token, requestLoja(nome)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private CadastrarLojaRequest requestLoja(String nome) {
        return new CadastrarLojaRequest(nome, "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP", null);
    }

    private Long criarSupermercadoComPlano(String cnpj, Integer limiteLojas) {
        Instant agora = Instant.now();
        SupermercadoJpaEntity supermercado = supermercadoJpaRepository.save(new SupermercadoJpaEntity(
                null, cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP",
                null, null, null, EstadoSupermercado.ATIVO, null, agora, agora
        ));

        PlanoJpaEntity plano = planoJpaRepository.save(new PlanoJpaEntity(
                null, "Plano " + cnpj, "plano " + cnpj, 30, BigDecimal.TEN, 100, limiteLojas, null, agora, agora, null
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

    private String tokenSuperAdmin() throws Exception {
        return autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN);
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
