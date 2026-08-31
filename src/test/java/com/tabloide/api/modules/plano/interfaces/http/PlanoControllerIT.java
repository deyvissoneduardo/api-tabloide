package com.tabloide.api.modules.plano.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaRepository;
import com.tabloide.api.modules.plano.interfaces.http.dto.AssociarPlanoRequest;
import com.tabloide.api.modules.plano.interfaces.http.dto.CriarPlanoRequest;
import com.tabloide.api.modules.plano.interfaces.http.dto.EditarPlanoRequest;
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

class PlanoControllerIT extends PlanoIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private PlanoJpaRepository planoJpaRepository;

    @Autowired
    private SupermercadoJpaRepository supermercadoJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCriarPlano() throws Exception {
        mockMvc.perform(post("/api/planos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CriarPlanoRequest("Plano Criar", 30, BigDecimal.valueOf(99.90), 100, 5))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Plano Criar"))
                .andExpect(jsonPath("$.versao").value(0))
                .andExpect(jsonPath("$.excluido").value(false));
    }

    @Test
    void deveRejeitarCriacaoComNomeDuplicado() throws Exception {
        String token = tokenSuperAdmin();
        criarPlano("Plano Duplicado", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(post("/api/planos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CriarPlanoRequest("  plano duplicado  ", 30, BigDecimal.valueOf(99.90), 100, null))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveListarPlanosPaginado() throws Exception {
        String token = tokenSuperAdmin();
        criarPlano("Plano Listar Um", 30, BigDecimal.valueOf(99.90), 100);
        criarPlano("Plano Listar Dois", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(get("/api/planos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("pagina", "0")
                        .param("tamanho", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tamanho").value(25));
    }

    @Test
    void deveBuscarPlanoPorId() throws Exception {
        Long planoId = criarPlano("Plano Buscar", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(get("/api/planos/" + planoId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(planoId));
    }

    @Test
    void deveRetornarNaoEncontradoAoBuscarPlanoInexistente() throws Exception {
        mockMvc.perform(get("/api/planos/999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveEditarPlano() throws Exception {
        Long planoId = criarPlano("Plano Editar", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(patch("/api/planos/" + planoId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EditarPlanoRequest(0L, "Plano Editado", 60, BigDecimal.valueOf(199.90), 500, 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Plano Editado"))
                .andExpect(jsonPath("$.versao").value(1));
    }

    @Test
    void deveRejeitarEdicaoComVersaoDesatualizada() throws Exception {
        Long planoId = criarPlano("Plano Versao", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(patch("/api/planos/" + planoId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EditarPlanoRequest(99L, "Plano Versao Nova", 60, BigDecimal.valueOf(199.90), 500, null))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarEdicaoComNomeDuplicado() throws Exception {
        String token = tokenSuperAdmin();
        criarPlano("Plano Existente", 30, BigDecimal.valueOf(99.90), 100);
        Long planoId = criarPlano("Plano A Editar", 30, BigDecimal.valueOf(99.90), 100);

        mockMvc.perform(patch("/api/planos/" + planoId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EditarPlanoRequest(0L, "Plano Existente", 60, BigDecimal.valueOf(199.90), 500, null))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveExcluirPlanoLogicamenteESerIdempotente() throws Exception {
        Long planoId = criarPlano("Plano Excluir", 30, BigDecimal.valueOf(99.90), 100);
        String token = tokenSuperAdmin();

        mockMvc.perform(delete("/api/planos/" + planoId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excluido").value(true));

        mockMvc.perform(delete("/api/planos/" + planoId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excluido").value(true));
    }

    @Test
    void deveRejeitarAssociacaoDePlanoExcluido() throws Exception {
        String token = tokenSuperAdmin();
        Long planoId = criarPlano("Plano Para Excluir Associar", 30, BigDecimal.valueOf(99.90), 100);
        Long supermercadoId = criarSupermercado(EstadoSupermercado.ATIVO, "11444777003004");

        mockMvc.perform(delete("/api/planos/" + planoId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/plano")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociarPlanoRequest(planoId))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCriacaoComOperador() throws Exception {
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-plano-catalogo@sgtm.local");

        mockMvc.perform(post("/api/planos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CriarPlanoRequest("Plano Operador", 30, BigDecimal.valueOf(99.90), 100, null))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarCriacaoSemToken() throws Exception {
        mockMvc.perform(post("/api/planos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CriarPlanoRequest("Plano Sem Token", 30, BigDecimal.valueOf(99.90), 100, null))))
                .andExpect(status().isUnauthorized());
    }

    private Long criarPlano(String nome, int validadeDias, BigDecimal valor, Integer limiteFotos) {
        Instant agora = Instant.now();
        PlanoJpaEntity entidade = new PlanoJpaEntity(
                null, nome, Plano.normalizarNome(nome), validadeDias, valor, limiteFotos, null, null, agora, agora, null
        );
        return planoJpaRepository.save(entidade).getId();
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
