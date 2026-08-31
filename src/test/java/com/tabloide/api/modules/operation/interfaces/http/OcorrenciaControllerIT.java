package com.tabloide.api.modules.operation.interfaces.http;

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
import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.SeveridadeOcorrencia;
import com.tabloide.api.modules.operation.interfaces.http.dto.AbrirOcorrenciaRequest;
import com.tabloide.api.modules.operation.interfaces.http.dto.AdicionarComentarioRequest;
import com.tabloide.api.modules.operation.interfaces.http.dto.TransicionarEstadoOcorrenciaRequest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class OcorrenciaControllerIT extends OcorrenciaIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveAbrirOcorrenciaComoSuperAdmin() throws Exception {
        mockMvc.perform(post("/api/ocorrencias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAbertura())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.estado").value("ABERTA"))
                .andExpect(jsonPath("$.versao").isNotEmpty());
    }

    @Test
    void deveRejeitarAberturaSemToken() throws Exception {
        mockMvc.perform(post("/api/ocorrencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAbertura())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarAberturaComOperador() throws Exception {
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-ocorrencia@sgtm.local");

        mockMvc.perform(post("/api/ocorrencias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAbertura())))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarAberturaComCamposInvalidos() throws Exception {
        AbrirOcorrenciaRequest invalido = new AbrirOcorrenciaRequest("", "", null, null, null);

        mockMvc.perform(post("/api/ocorrencias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveTransicionarDeAbertaParaEmAnalise() throws Exception {
        String token = tokenSuperAdmin();
        long id = abrirOcorrencia(token);

        mockMvc.perform(patch("/api/ocorrencias/" + id + "/estado")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransicionarEstadoOcorrenciaRequest(EstadoOcorrencia.EM_ANALISE, "iniciando", 0L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EM_ANALISE"));
    }

    @Test
    void deveRejeitarTransicaoQuePulaEstado() throws Exception {
        String token = tokenSuperAdmin();
        long id = abrirOcorrencia(token);

        mockMvc.perform(patch("/api/ocorrencias/" + id + "/estado")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransicionarEstadoOcorrenciaRequest(EstadoOcorrencia.RESOLVIDA, null, 0L))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarTransicaoComVersaoDesatualizada() throws Exception {
        String token = tokenSuperAdmin();
        long id = abrirOcorrencia(token);

        mockMvc.perform(patch("/api/ocorrencias/" + id + "/estado")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransicionarEstadoOcorrenciaRequest(EstadoOcorrencia.EM_ANALISE, null, 999L))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveAdicionarComentarioSemAlterarEstado() throws Exception {
        String token = tokenSuperAdmin();
        long id = abrirOcorrencia(token);

        mockMvc.perform(post("/api/ocorrencias/" + id + "/comentarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AdicionarComentarioRequest("comentário de suporte"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ABERTA"));
    }

    @Test
    void deveBuscarOcorrenciaPorIdComHistorico() throws Exception {
        String token = tokenSuperAdmin();
        long id = abrirOcorrencia(token);

        mockMvc.perform(get("/api/ocorrencias/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ocorrencia.id").value(id))
                .andExpect(jsonPath("$.historico.length()").value(1))
                .andExpect(jsonPath("$.historico[0].tipo").value("TRANSICAO_ESTADO"));
    }

    @Test
    void deveRetornarNaoEncontradoParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/ocorrencias/999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarOcorrenciasComFiltroDeStatus() throws Exception {
        String token = tokenSuperAdmin();
        abrirOcorrencia(token);

        mockMvc.perform(get("/api/ocorrencias")
                        .param("status", "ABERTA")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens").isNotEmpty());
    }

    private long abrirOcorrencia(String token) throws Exception {
        String corpo = mockMvc.perform(post("/api/ocorrencias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAbertura())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private AbrirOcorrenciaRequest requestAbertura() {
        return new AbrirOcorrenciaRequest("Título de suporte", "Descrição do problema relatado", SeveridadeOcorrencia.ALTA, null, null);
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
