package com.tabloide.api.modules.analytics.interfaces.http;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

class MetricasControllerIT extends AnalyticsIntegrationTestSupport {

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
    void deveExigirPerfilSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777011015");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-metricas@sgtm.local");

        mockMvc.perform(get("/api/metricas/supermercados/mais-utilizados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRankearSupermercadoComMaisAcessosPrimeiro() throws Exception {
        // US-010: identificar supermercados com maior utilização da plataforma.
        Long supermercadoMaisAcessado = criarSupermercadoComPlano("11444777011104");
        String tokenA = criarDonoEAutenticar(supermercadoMaisAcessado, "dono-mais-acessado@sgtm.local");
        long lojaA = cadastrarLojaECapturarId(supermercadoMaisAcessado, tokenA, "Loja A");

        Long supermercadoMenosAcessado = criarSupermercadoComPlano("11444777011287");
        String tokenB = criarDonoEAutenticar(supermercadoMenosAcessado, "dono-menos-acessado@sgtm.local");
        long lojaB = cadastrarLojaECapturarId(supermercadoMenosAcessado, tokenB, "Loja B");

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/publico/lojas/" + lojaA)).andExpect(status().isOk());
        }
        mockMvc.perform(get("/api/publico/lojas/" + lojaB)).andExpect(status().isOk());

        mockMvc.perform(get("/api/metricas/supermercados/mais-utilizados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .param("inicio", Instant.now().minusSeconds(3600).toString())
                        .param("fim", Instant.now().plusSeconds(60).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].supermercadoId").value(supermercadoMaisAcessado))
                .andExpect(jsonPath("$[0].quantidadeEventosPeriodoAtual").value(3));
    }

    @Test
    void deveIdentificarSupermercadoSemEventosComoInativo() throws Exception {
        // US-011: identificar supermercados inativos ou com baixa utilização.
        Long supermercadoInativo = criarSupermercadoComPlano("11444777011368");

        mockMvc.perform(get("/api/metricas/supermercados/baixa-utilizacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .param("inicio", Instant.now().minusSeconds(3600).toString())
                        .param("fim", Instant.now().plusSeconds(60).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[?(@.supermercadoId == " + supermercadoInativo + ")].classificacao").value("INATIVO"));
    }

    @Test
    void deveExportarMaisUtilizadosEmCsv() throws Exception {
        // US-222/RN-014: exportar relatórios em CSV.
        Long supermercadoId = criarSupermercadoComPlano("11444777055063");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-metricas-csv@sgtm.local");
        long loja = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja A");
        mockMvc.perform(get("/api/publico/lojas/" + loja)).andExpect(status().isOk());

        mockMvc.perform(get("/api/metricas/supermercados/mais-utilizados/csv")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .param("inicio", Instant.now().minusSeconds(3600).toString())
                        .param("fim", Instant.now().plusSeconds(60).toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(content().string(startsWith(
                        "supermercadoId,nomeFantasia,quantidadeEventosPeriodoAtual,quantidadeEventosPeriodoAnterior,variacaoPercentual\r\n")))
                .andExpect(content().string(containsString(supermercadoId + ",Mercado Bom Preço,1,0,")));
    }

    @Test
    void deveExigirPerfilSuperAdminParaExportarCsv() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777063163");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-metricas-csv-negado@sgtm.local");

        mockMvc.perform(get("/api/metricas/supermercados/baixa-utilizacao/csv")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isForbidden());
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
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode(SENHA_PADRAO), Perfil.DONO, supermercadoId, null, true, 0, null, agora, agora
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
