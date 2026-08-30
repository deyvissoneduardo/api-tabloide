package com.tabloide.api.modules.autenticacao.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.infrastructure.security.JwtTokenService;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class SessaoControllerIT extends AutenticacaoIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void deveAutenticarComCredenciaisCorretas() throws Exception {
        mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.perfil").value("SUPER_ADMIN"));
    }

    @Test
    void deveRejeitarSenhaIncorreta() throws Exception {
        mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL_SUPER_ADMIN, "senhaErrada1"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarRotaProtegidaSemToken() throws Exception {
        mockMvc.perform(delete("/api/sessoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveBloquearTemporariamenteAposCincoTentativasInvalidas() throws Exception {
        String email = "bloqueio-teste@sgtm.local";
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, email, "SenhaCorreta1", "11444777000161");

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/sessoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginRequest(email, "senhaErrada1"))))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "SenhaCorreta1"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutDeveRevogarSessaoEBloquearReutilizacaoDoToken() throws Exception {
        String corpo = mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        LoginResponse resposta = objectMapper.readValue(corpo, LoginResponse.class);

        mockMvc.perform(delete("/api/sessoes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + resposta.token()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/sessoes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + resposta.token()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarListagemDeSessoesSemToken() throws Exception {
        mockMvc.perform(get("/api/sessoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveNegarListagemDeSessoesParaOperador() throws Exception {
        UsuarioDeTesteFactory.criarOperador(usuarioJpaRepository, passwordEncoder, "operador@sgtm.local", "SenhaCorreta1", "11444777000161", 30L);
        String token = autenticarEExtrairToken("operador@sgtm.local", "SenhaCorreta1");

        mockMvc.perform(get("/api/sessoes").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void superAdminDeveVerSessoesDeQualquerSupermercado() throws Exception {
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, "dono-a@sgtm.local", "SenhaCorreta1", "11444777000161", 40L);
        autenticarEExtrairToken("dono-a@sgtm.local", "SenhaCorreta1");
        String tokenSuperAdmin = autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN);

        mockMvc.perform(get("/api/sessoes").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[?(@.email == 'dono-a@sgtm.local')]").exists());
    }

    @Test
    void donoDeveVerApenasSessoesDoProprioSupermercado() throws Exception {
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, "dono-b@sgtm.local", "SenhaCorreta1", "11444777000161", 50L);
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, "dono-c@sgtm.local", "SenhaCorreta1", "11222333000181", 60L);
        String tokenDonoB = autenticarEExtrairToken("dono-b@sgtm.local", "SenhaCorreta1");
        autenticarEExtrairToken("dono-c@sgtm.local", "SenhaCorreta1");

        mockMvc.perform(get("/api/sessoes").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDonoB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[?(@.email == 'dono-b@sgtm.local')]").exists())
                .andExpect(jsonPath("$.itens[?(@.email == 'dono-c@sgtm.local')]").doesNotExist());
    }

    @Test
    void deveRejeitarTamanhoDePaginaForaDaListaPermitida() throws Exception {
        mockMvc.perform(get("/api/sessoes?tamanho=10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void donoNaoDeveEncontrarSessaoDeOutroSupermercadoPorId() throws Exception {
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, "dono-d@sgtm.local", "SenhaCorreta1", "11444777000161", 70L);
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, "dono-e@sgtm.local", "SenhaCorreta1", "11222333000181", 80L);
        String tokenDonoD = autenticarEExtrairToken("dono-d@sgtm.local", "SenhaCorreta1");
        String tokenDonoE = autenticarEExtrairToken("dono-e@sgtm.local", "SenhaCorreta1");
        String jtiDonoE = jwtTokenService.decodificar(tokenDonoE).jti();

        mockMvc.perform(get("/api/sessoes/" + jtiDonoE).header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDonoD))
                .andExpect(status().isNotFound());
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
