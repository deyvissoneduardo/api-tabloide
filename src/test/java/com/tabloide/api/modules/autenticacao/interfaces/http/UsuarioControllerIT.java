package com.tabloide.api.modules.autenticacao.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaEntity;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioControllerIT extends AutenticacaoIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private SupermercadoJpaRepository supermercadoJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveListarUsuariosDoSupermercadoComoSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001080");
        criarUsuario(Perfil.DONO, supermercadoId, "dono-lista@sgtm.local");
        criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-lista@sgtm.local");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(2));
    }

    @Test
    void deveRetornarListaVaziaParaSupermercadoSemUsuarios() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001161");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(0));
    }

    @Test
    void deveRejeitarListagemComDono() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001242");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-rejeitado@sgtm.local");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarListagemSemToken() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001323");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    private Long criarSupermercado(String cnpj) {
        Instant agora = Instant.now();
        SupermercadoJpaEntity entidade = new SupermercadoJpaEntity(
                null, cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP",
                null, null, null, EstadoSupermercado.ATIVO, null, agora, agora
        );
        return supermercadoJpaRepository.save(entidade).getId();
    }

    private void criarUsuario(Perfil perfil, Long supermercadoId, String email) {
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode("SenhaCorreta1"), perfil, supermercadoId, null, true, 0, null, agora, agora
        ));
    }

    private String tokenSuperAdmin() throws Exception {
        return autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN);
    }

    private String criarUsuarioEAutenticar(Perfil perfil, Long supermercadoId, String email) throws Exception {
        String senha = "SenhaCorreta1";
        criarUsuario(perfil, supermercadoId, email);
        return autenticarEExtrairToken(email, senha);
    }

    private String autenticarEExtrairToken(String email, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/sessoes")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, senha))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(corpo, LoginResponse.class).token();
    }
}
