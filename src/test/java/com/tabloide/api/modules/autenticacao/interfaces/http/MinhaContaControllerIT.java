package com.tabloide.api.modules.autenticacao.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.AlterarPropriosDadosRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class MinhaContaControllerIT extends AutenticacaoIntegrationTestSupport {

    private static final String SENHA_PADRAO = "SenhaCorreta1";

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveTrocarSenhaERevogarSessaoAtual() throws Exception {
        String email = "dono-troca-senha@sgtm.local";
        String token = criarUsuarioEAutenticar(Perfil.DONO, email);

        mockMvc.perform(alterar(token, new AlterarPropriosDadosRequest(SENHA_PADRAO, null, "NovaSenha1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        mockMvc.perform(alterar(token, new AlterarPropriosDadosRequest("NovaSenha1", null, "OutraSenha1")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveTrocarApenasEmailSemDerrubarSessaoAtual() throws Exception {
        String token = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-troca-email@sgtm.local");

        mockMvc.perform(alterar(token, new AlterarPropriosDadosRequest(SENHA_PADRAO, "novo-email@sgtm.local", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("novo-email@sgtm.local"));

        mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("novo-email@sgtm.local", SENHA_PADRAO))))
                .andExpect(status().isOk());
    }

    @Test
    void deveRejeitarQuandoSenhaAtualIncorreta() throws Exception {
        String token = criarUsuarioEAutenticar(Perfil.DONO, "dono-senha-errada@sgtm.local");

        mockMvc.perform(alterar(token, new AlterarPropriosDadosRequest("SenhaErrada1", null, "NovaSenha1")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarQuandoNenhumaAlteracaoInformada() throws Exception {
        String token = criarUsuarioEAutenticar(Perfil.DONO, "dono-sem-alteracao@sgtm.local");

        mockMvc.perform(alterar(token, new AlterarPropriosDadosRequest(SENHA_PADRAO, null, null)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarEmailJaCadastradoPorOutroUsuario() throws Exception {
        criarUsuarioEAutenticar(Perfil.OPERADOR, "email-existente@sgtm.local");
        String token = criarUsuarioEAutenticar(Perfil.DONO, "dono-email-duplicado@sgtm.local");

        mockMvc.perform(alterar(token, new AlterarPropriosDadosRequest(SENHA_PADRAO, "email-existente@sgtm.local", null)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarSemToken() throws Exception {
        mockMvc.perform(patch("/api/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AlterarPropriosDadosRequest(SENHA_PADRAO, null, "NovaSenha1"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarComSuperAdmin() throws Exception {
        String token = autenticarEExtrairToken("superadmin@sgtm.local", "SuperAdmin@123");

        mockMvc.perform(alterar(token, new AlterarPropriosDadosRequest("SuperAdmin@123", null, "NovaSenha1")))
                .andExpect(status().isForbidden());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder alterar(
            String token, AlterarPropriosDadosRequest request) throws Exception {
        return patch("/api/usuarios/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private String criarUsuarioEAutenticar(Perfil perfil, String email) throws Exception {
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode(SENHA_PADRAO), perfil, null, null, true, 0, null, agora, agora
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
