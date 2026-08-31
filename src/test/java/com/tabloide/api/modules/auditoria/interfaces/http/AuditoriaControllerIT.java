package com.tabloide.api.modules.auditoria.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.supermercado.interfaces.http.dto.CadastrarSupermercadoRequest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuditoriaControllerIT extends AuditoriaIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveRejeitarListagemSemToken() throws Exception {
        mockMvc.perform(get("/api/auditoria"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarListagemComOperador() throws Exception {
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, null, "operador-auditoria@sgtm.local");

        mockMvc.perform(get("/api/auditoria")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador))
                .andExpect(status().isForbidden());
    }

    @Test
    void superAdminDeveListarRegistroGeradoPeloCadastroDeSupermercado() throws Exception {
        String token = tokenSuperAdmin();
        cadastrarSupermercado(token, "11222333000181");

        mockMvc.perform(get("/api/auditoria")
                        .param("acao", "SUPERMERCADO_CADASTRADO")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].acao").value("SUPERMERCADO_CADASTRADO"))
                .andExpect(jsonPath("$.itens[0].entidade").value("Supermercado"));
    }

    @Test
    void donoDeveVerApenasRegistrosDoProprioSupermercado() throws Exception {
        String tokenSuperAdmin = tokenSuperAdmin();
        long supermercadoDono = cadastrarSupermercado(tokenSuperAdmin, "11444777000161");
        cadastrarSupermercado(tokenSuperAdmin, "11444777000242");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoDono, "dono-auditoria@sgtm.local");

        mockMvc.perform(get("/api/auditoria")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(1))
                .andExpect(jsonPath("$.itens[0].supermercadoId").value(supermercadoDono));
    }

    @Test
    void deveBuscarRegistroPorIdDentroDoEscopo() throws Exception {
        String token = tokenSuperAdmin();
        cadastrarSupermercado(token, "11444777000323");
        long registroId = idDoPrimeiroRegistro(token);

        mockMvc.perform(get("/api/auditoria/" + registroId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registroId));
    }

    @Test
    void deveRetornarNaoEncontradoParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/auditoria/999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void donoNaoDeveBuscarRegistroDeOutroSupermercado() throws Exception {
        String tokenSuperAdmin = tokenSuperAdmin();
        cadastrarSupermercado(tokenSuperAdmin, "11444777000404");
        long registroDeOutroSupermercado = idDoPrimeiroRegistro(tokenSuperAdmin);
        long supermercadoDono = cadastrarSupermercado(tokenSuperAdmin, "11444777000595");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoDono, "dono-busca-auditoria@sgtm.local");

        mockMvc.perform(get("/api/auditoria/" + registroDeOutroSupermercado)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarTamanhoDePaginaInvalido() throws Exception {
        mockMvc.perform(get("/api/auditoria")
                        .param("tamanho", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isUnprocessableEntity());
    }

    private long idDoPrimeiroRegistro(String token) throws Exception {
        String corpo = mockMvc.perform(get("/api/auditoria")
                        .param("tamanho", "25")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("itens").get(0).get("id").asLong();
    }

    private long cadastrarSupermercado(String token, String cnpj) throws Exception {
        CadastrarSupermercadoRequest request = new CadastrarSupermercadoRequest(
                cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP", null, null, null
        );
        String corpo = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private String tokenSuperAdmin() throws Exception {
        return autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN);
    }

    private String criarUsuarioEAutenticar(Perfil perfil, Long supermercadoId, String email) throws Exception {
        String senha = "SenhaCorreta1";
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode(senha), perfil, supermercadoId, null, true, 0, null, agora, agora
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
