package com.tabloide.api.modules.plano.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaRepository;
import com.tabloide.api.modules.plano.interfaces.http.dto.AssociarPlanoRequest;
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

class AssinaturaControllerIT extends PlanoIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private SupermercadoJpaRepository supermercadoJpaRepository;

    @Autowired
    private PlanoJpaRepository planoJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveAssociarPlanoAoSupermercadoAtivo() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11222333000181");
        Long planoId = criarPlano("Básico Ativo", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("VIGENTE"))
                .andExpect(jsonPath("$.planoId").value(planoId))
                .andExpect(jsonPath("$.planoNome").value("Básico Ativo"));
    }

    @Test
    void deveRejeitarAssociacaoSemToken() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777000161");
        Long planoId = criarPlano("Básico SemToken", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarAssociacaoComOperador() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777000242");
        Long planoId = criarPlano("Básico Operador", 30, BigDecimal.valueOf(99.90), 100);
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-plano@sgtm.local");

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarAssociacaoQuandoSupermercadoNaoExiste() throws Exception {
        Long planoId = criarPlano("Básico SupNaoExiste", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(post("/api/supermercados/999999/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarAssociacaoQuandoPlanoNaoExiste() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777000323");

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(999999L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarAssociacaoQuandoSupermercadoBloqueado() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.BLOQUEADO, "11444777000404");
        Long planoId = criarPlano("Básico Bloqueado", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarAssociacaoQuandoJaExisteAssinaturaVigente() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777000595");
        Long planoId = criarPlano("Básico JaExiste", 30, BigDecimal.valueOf(99.90), 100);
        String token = tokenSuperAdmin();

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveAlterarPlanoVigente() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777000676");
        Long planoAntigoId = criarPlano("Básico Alterar", 30, BigDecimal.valueOf(99.90), 100);
        Long planoNovoId = criarPlano("Premium Alterar", 60, BigDecimal.valueOf(199.90), 500);
        String token = tokenSuperAdmin();

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoAntigoId))))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoNovoId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("VIGENTE"))
                .andExpect(jsonPath("$.planoId").value(planoNovoId))
                .andExpect(jsonPath("$.planoNome").value("Premium Alterar"));
    }

    @Test
    void deveRejeitarAlteracaoQuandoNaoHaAssinaturaVigente() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777000757");
        Long planoId = criarPlano("Básico SemVigente", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(patch("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveBuscarAssinaturaVigenteComoSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777000838");
        Long planoId = criarPlano("Básico Consulta", 30, BigDecimal.valueOf(99.90), 100);
        String token = tokenSuperAdmin();
        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planoId").value(planoId));
    }

    @Test
    void deveRetornarNaoEncontradoQuandoSupermercadoNaoPossuiAssinatura() throws Exception {
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777000919");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isNotFound());
    }

    private Long criarSupermercado(EstadoSupermercado estado, String cnpj) {
        Instant agora = Instant.now();
        SupermercadoJpaEntity entidade = new SupermercadoJpaEntity(
                null, cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP",
                null, null, null, estado, null, agora, agora
        );
        return supermercadoJpaRepository.save(entidade).getId();
    }

    private Long criarPlano(String nome, int validadeDias, BigDecimal valor, Integer limiteFotos) {
        PlanoJpaEntity entidade = new PlanoJpaEntity(null, nome, validadeDias, valor, limiteFotos);
        return planoJpaRepository.save(entidade).getId();
    }

    private String tokenSuperAdmin() throws Exception {
        return autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN);
    }

    private String criarUsuarioEAutenticar(Perfil perfil, String email) throws Exception {
        String senha = "SenhaCorreta1";
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode(senha), perfil, null, null, true, 0, null, agora, agora
        ));
        return autenticarEExtrairToken(email, senha);
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
