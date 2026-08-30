package com.tabloide.api.modules.autenticacao.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.RedefinirSenhaRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.VerificarRedefinicaoSenhaRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.VerificarRedefinicaoSenhaResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class RedefinicaoSenhaControllerIT extends AutenticacaoIntegrationTestSupport {

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveEmitirTokenQuandoCnpjEEmailConferem() throws Exception {
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, "redefinicao-ok@sgtm.local", "SenhaAntiga1", "11222333000181");

        mockMvc.perform(post("/api/redefinicoes-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VerificarRedefinicaoSenhaRequest("11222333000181", "redefinicao-ok@sgtm.local"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void deveRetornar404QuandoCnpjNaoConfere() throws Exception {
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, "redefinicao-cnpj-errado@sgtm.local", "SenhaAntiga1", "11444777000161");

        mockMvc.perform(post("/api/redefinicoes-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VerificarRedefinicaoSenhaRequest("11222333000181", "redefinicao-cnpj-errado@sgtm.local"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveTrocarSenhaERevogarSessoesAtivas() throws Exception {
        String email = "redefinicao-fluxo@sgtm.local";
        String cnpj = "12345678000195";
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, email, "SenhaAntiga1", cnpj);

        String corpoLogin = mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "SenhaAntiga1"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        LoginResponse sessaoAntiga = objectMapper.readValue(corpoLogin, LoginResponse.class);

        String corpoVerificacao = mockMvc.perform(post("/api/redefinicoes-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VerificarRedefinicaoSenhaRequest(cnpj, email))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        VerificarRedefinicaoSenhaResponse verificacao = objectMapper.readValue(corpoVerificacao, VerificarRedefinicaoSenhaResponse.class);

        mockMvc.perform(put("/api/redefinicoes-senha/{token}", verificacao.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RedefinirSenhaRequest("SenhaNova1"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/sessoes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + sessaoAntiga.token()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "SenhaAntiga1"))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "SenhaNova1"))))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar404AoReutilizarTokenJaUsado() throws Exception {
        String email = "redefinicao-reuso@sgtm.local";
        String cnpj = "22233344000183";
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, email, "SenhaAntiga1", cnpj);

        String corpoVerificacao = mockMvc.perform(post("/api/redefinicoes-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VerificarRedefinicaoSenhaRequest(cnpj, email))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        VerificarRedefinicaoSenhaResponse verificacao = objectMapper.readValue(corpoVerificacao, VerificarRedefinicaoSenhaResponse.class);

        mockMvc.perform(put("/api/redefinicoes-senha/{token}", verificacao.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RedefinirSenhaRequest("SenhaNova1"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/api/redefinicoes-senha/{token}", verificacao.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RedefinirSenhaRequest("OutraSenha1"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar422QuandoNovaSenhaNaoAtendeAPolitica() throws Exception {
        String email = "redefinicao-senha-fraca@sgtm.local";
        String cnpj = "55566677000183";
        UsuarioDeTesteFactory.criarDono(usuarioJpaRepository, passwordEncoder, email, "SenhaAntiga1", cnpj);

        String corpoVerificacao = mockMvc.perform(post("/api/redefinicoes-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VerificarRedefinicaoSenhaRequest(cnpj, email))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        VerificarRedefinicaoSenhaResponse verificacao = objectMapper.readValue(corpoVerificacao, VerificarRedefinicaoSenhaResponse.class);

        mockMvc.perform(put("/api/redefinicoes-senha/{token}", verificacao.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RedefinirSenhaRequest("fraca"))))
                .andExpect(status().isUnprocessableEntity());
    }
}
