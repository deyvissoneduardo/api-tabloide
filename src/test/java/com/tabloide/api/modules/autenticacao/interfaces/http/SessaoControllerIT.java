package com.tabloide.api.modules.autenticacao.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
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
}
