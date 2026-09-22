package com.tabloide.api.modules.supermercado.interfaces.http;

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
import com.tabloide.api.modules.supermercado.interfaces.http.dto.CadastrarSupermercadoRequest;
import com.tabloide.api.modules.supermercado.interfaces.http.dto.EditarSupermercadoRequest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class SupermercadoControllerIT extends SupermercadoIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCadastrarSupermercadoComoSuperAdmin() throws Exception {
        mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11222333000181"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.estado").value("ATIVO"))
                .andExpect(jsonPath("$.versao").isNotEmpty());
    }

    @Test
    void deveRejeitarCadastroSemToken() throws Exception {
        mockMvc.perform(post("/api/supermercados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000161"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarCadastroComOperador() throws Exception {
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-cadastro@sgtm.local");

        mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000161"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarCadastroComCamposInvalidos() throws Exception {
        CadastrarSupermercadoRequest invalido = new CadastrarSupermercadoRequest(
                "cnpj-invalido", "", "Fantasia", "email-invalido", "119999",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP", null, null, null
        );

        mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void deveRejeitarCnpjDuplicado() throws Exception {
        String token = tokenSuperAdmin();
        mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11987650001080"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11987650001080"))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveEditarSupermercadoComVersaoAtual() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11321741000190"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();
        long versao = objectMapper.readTree(corpoCadastro).get("versao").asLong();

        EditarSupermercadoRequest edicao = new EditarSupermercadoRequest(
                versao, "Razão Editada", "Fantasia Editada", "editado@mercado.com", "11777776666",
                "01310-100", "Av. Paulista", "2000", "Bela Vista", "São Paulo", "SP", null, null, null
        );

        mockMvc.perform(patch("/api/supermercados/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(edicao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial").value("Razão Editada"));
    }

    @Test
    void deveEditarSupermercadoComoDonoDoProprioSupermercado() throws Exception {
        String tokenSuperAdmin = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777004230"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();
        long versao = objectMapper.readTree(corpoCadastro).get("versao").asLong();
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, "dono-edicao@sgtm.local", id);

        EditarSupermercadoRequest edicao = new EditarSupermercadoRequest(
                versao, "Razão Editada Pelo Dono", "Fantasia Editada", "editado@mercado.com", "11777776666",
                "01310-100", "Av. Paulista", "2000", "Bela Vista", "São Paulo", "SP", null, null, null
        );

        mockMvc.perform(patch("/api/supermercados/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(edicao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razaoSocial").value("Razão Editada Pelo Dono"));
    }

    @Test
    void deveRejeitarEdicaoDeOutroSupermercadoComoDono() throws Exception {
        String tokenSuperAdmin = tokenSuperAdmin();
        String corpoCadastroA = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777005392"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long idSupermercadoA = objectMapper.readTree(corpoCadastroA).get("id").asLong();

        String corpoCadastroB = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777006445"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long idSupermercadoB = objectMapper.readTree(corpoCadastroB).get("id").asLong();
        long versaoB = objectMapper.readTree(corpoCadastroB).get("versao").asLong();

        String tokenDonoA = criarUsuarioEAutenticar(Perfil.DONO, "dono-escopo@sgtm.local", idSupermercadoA);

        EditarSupermercadoRequest edicao = new EditarSupermercadoRequest(
                versaoB, "Razão Editada", "Fantasia Editada", "editado@mercado.com", "11777776666",
                "01310-100", "Av. Paulista", "2000", "Bela Vista", "São Paulo", "SP", null, null, null
        );

        mockMvc.perform(patch("/api/supermercados/" + idSupermercadoB)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDonoA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(edicao)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarEdicaoComOperador() throws Exception {
        String tokenSuperAdmin = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777007506"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();
        long versao = objectMapper.readTree(corpoCadastro).get("versao").asLong();
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-edicao@sgtm.local", id);

        EditarSupermercadoRequest edicao = new EditarSupermercadoRequest(
                versao, "Razão Editada", "Fantasia Editada", "editado@mercado.com", "11777776666",
                "01310-100", "Av. Paulista", "2000", "Bela Vista", "São Paulo", "SP", null, null, null
        );

        mockMvc.perform(patch("/api/supermercados/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(edicao)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarEdicaoComVersaoDesatualizada() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000242"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();

        EditarSupermercadoRequest edicaoComVersaoErrada = new EditarSupermercadoRequest(
                999L, "Razão Editada", "Fantasia Editada", "editado@mercado.com", "11777776666",
                "01310-100", "Av. Paulista", "2000", "Bela Vista", "São Paulo", "SP", null, null, null
        );

        mockMvc.perform(patch("/api/supermercados/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(edicaoComVersaoErrada)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveDesativarEReativarSupermercado() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11055978000177"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();

        mockMvc.perform(post("/api/supermercados/" + id + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DESATIVADO"));

        mockMvc.perform(post("/api/supermercados/" + id + "/ativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATIVO"));
    }

    @Test
    void deveSerIdempotenteAoAtivarSupermercadoJaAtivo() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000323"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();

        mockMvc.perform(post("/api/supermercados/" + id + "/ativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATIVO"));
    }

    @Test
    void deveRetornarNaoEncontradoParaIdInexistente() throws Exception {
        mockMvc.perform(post("/api/supermercados/999999/ativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBloquearEReativarSupermercado() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000404"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();

        mockMvc.perform(post("/api/supermercados/" + id + "/bloqueio")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("BLOQUEADO"));

        mockMvc.perform(post("/api/supermercados/" + id + "/ativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATIVO"));
    }

    @Test
    void deveRejeitarBloqueioComOperador() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000595"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-bloqueio@sgtm.local");

        mockMvc.perform(post("/api/supermercados/" + id + "/bloqueio")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarBloqueioQuandoJaDesativado() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000676"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();

        mockMvc.perform(post("/api/supermercados/" + id + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/supermercados/" + id + "/bloqueio")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isConflict());
    }

    @Test
    void deveSerIdempotenteAoBloquearSupermercadoJaBloqueado() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000757"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();

        mockMvc.perform(post("/api/supermercados/" + id + "/bloqueio")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/supermercados/" + id + "/bloqueio")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("BLOQUEADO"));
    }

    @Test
    void deveBuscarSupermercadoPorIdComoSuperAdmin() throws Exception {
        String token = tokenSuperAdmin();
        String corpoCadastro = mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000838"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpoCadastro).get("id").asLong();

        mockMvc.perform(get("/api/supermercados/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.estado").value("ATIVO"));
    }

    @Test
    void deveRetornarNaoEncontradoAoBuscarSupermercadoInexistente() throws Exception {
        mockMvc.perform(get("/api/supermercados/999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarBuscaPorIdComOperador() throws Exception {
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-busca@sgtm.local");

        mockMvc.perform(get("/api/supermercados/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarBuscaPorIdSemToken() throws Exception {
        mockMvc.perform(get("/api/supermercados/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveListarSupermercadosComPaginacaoPadrao() throws Exception {
        String token = tokenSuperAdmin();
        mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastro("11444777000919"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.tamanho").value(25))
                .andExpect(jsonPath("$.itens").isArray());
    }

    @Test
    void deveListarSupermercadosOrdenadosPorRazaoSocial() throws Exception {
        String token = tokenSuperAdmin();
        mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastroComRazaoSocial("11222333000262", "Zeta Supermercados"))))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCadastroComRazaoSocial("11222333000343", "Alfa Supermercados"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("tamanho", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].razaoSocial").value("Alfa Supermercados"));
    }

    @Test
    void deveRejeitarTamanhoDePaginaInvalidoNaListagem() throws Exception {
        mockMvc.perform(get("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .param("tamanho", "10"))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void deveRejeitarPaginaNegativaNaListagem() throws Exception {
        mockMvc.perform(get("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .param("pagina", "-1"))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void deveRejeitarListagemSemToken() throws Exception {
        mockMvc.perform(get("/api/supermercados"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarListagemComOperador() throws Exception {
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-listagem@sgtm.local");

        mockMvc.perform(get("/api/supermercados")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador))
                .andExpect(status().isForbidden());
    }

    private CadastrarSupermercadoRequest requestCadastro(String cnpj) {
        return new CadastrarSupermercadoRequest(
                cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP", null, null, null
        );
    }

    private CadastrarSupermercadoRequest requestCadastroComRazaoSocial(String cnpj, String razaoSocial) {
        return new CadastrarSupermercadoRequest(
                cnpj, razaoSocial, "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP", null, null, null
        );
    }

    private String tokenSuperAdmin() throws Exception {
        return autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN);
    }

    private String criarUsuarioEAutenticar(Perfil perfil, String email) throws Exception {
        return criarUsuarioEAutenticar(perfil, email, null);
    }

    private String criarUsuarioEAutenticar(Perfil perfil, String email, Long supermercadoId) throws Exception {
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
