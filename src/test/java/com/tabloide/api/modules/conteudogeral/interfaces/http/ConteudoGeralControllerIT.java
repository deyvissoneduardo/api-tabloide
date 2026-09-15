package com.tabloide.api.modules.conteudogeral.interfaces.http;

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
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.interfaces.http.dto.CriarConteudoGeralRequest;
import com.tabloide.api.modules.conteudogeral.interfaces.http.dto.EditarConteudoGeralRequest;
import com.tabloide.api.modules.conteudogeral.interfaces.http.dto.TransicaoConteudoGeralRequest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class ConteudoGeralControllerIT extends ConteudoGeralIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCriarConteudoGeralEmRascunho() throws Exception {
        mockMvc.perform(criar(tokenSuperAdmin(), TipoConteudoGeral.TERMOS_USO, "Termos de uso", "Corpo dos termos"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RASCUNHO"))
                .andExpect(jsonPath("$.versao").value(0));
    }

    @Test
    void deveRejeitarCriacaoComOperador() throws Exception {
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, "operador-conteudo-geral@sgtm.local");

        mockMvc.perform(criar(tokenOperador, TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarCriacaoSemToken() throws Exception {
        mockMvc.perform(post("/api/conteudos-gerais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CriarConteudoGeralRequest(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveEditarConteudoEmRascunho() throws Exception {
        String token = tokenSuperAdmin();
        Long id = criarECapturarId(token, TipoConteudoGeral.CONTATO_SUPORTE, "Contato", "Corpo original");

        mockMvc.perform(patch("/api/conteudos-gerais/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EditarConteudoGeralRequest(0L, "Contato editado", "Corpo editado"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Contato editado"))
                .andExpect(jsonPath("$.versao").value(1));
    }

    @Test
    void devePublicarEDepoisArquivar() throws Exception {
        String token = tokenSuperAdmin();
        Long id = criarECapturarId(token, TipoConteudoGeral.POLITICA_PRIVACIDADE, "Política", "Corpo");

        mockMvc.perform(post("/api/conteudos-gerais/" + id + "/publicacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransicaoConteudoGeralRequest(0L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PUBLICADO"));

        mockMvc.perform(post("/api/conteudos-gerais/" + id + "/arquivamento")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransicaoConteudoGeralRequest(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ARQUIVADO"));
    }

    @Test
    void devePublicarNovaVersaoEArquivarAPublicadaAnteriorDoMesmoTipo() throws Exception {
        String token = tokenSuperAdmin();
        Long idAntigo = criarECapturarId(token, TipoConteudoGeral.COMUNICADO_GERAL, "Comunicado v1", "Corpo v1");
        publicar(token, idAntigo, 0L);

        Long idNovo = criarECapturarId(token, TipoConteudoGeral.COMUNICADO_GERAL, "Comunicado v2", "Corpo v2");
        mockMvc.perform(post("/api/conteudos-gerais/" + idNovo + "/publicacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransicaoConteudoGeralRequest(0L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PUBLICADO"));

        mockMvc.perform(get("/api/conteudos-gerais/" + idAntigo)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ARQUIVADO"));
    }

    @Test
    void naoDeveEditarConteudoPublicado() throws Exception {
        String token = tokenSuperAdmin();
        Long id = criarECapturarId(token, TipoConteudoGeral.CONTATO_SUPORTE, "Contato", "Corpo");
        publicar(token, id, 0L);

        mockMvc.perform(patch("/api/conteudos-gerais/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EditarConteudoGeralRequest(1L, "Novo", "Novo"))))
                .andExpect(status().isConflict());
    }

    private void publicar(String token, Long id, Long versao) throws Exception {
        mockMvc.perform(post("/api/conteudos-gerais/" + id + "/publicacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransicaoConteudoGeralRequest(versao))))
                .andExpect(status().isOk());
    }

    private Long criarECapturarId(String token, TipoConteudoGeral tipo, String titulo, String corpo) throws Exception {
        String corpoResposta = mockMvc.perform(criar(token, tipo, titulo, corpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpoResposta).get("id").asLong();
    }

    private MockHttpServletRequestBuilder criar(String token, TipoConteudoGeral tipo, String titulo, String corpo) throws Exception {
        return post("/api/conteudos-gerais")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CriarConteudoGeralRequest(tipo, titulo, corpo)));
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
