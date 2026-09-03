package com.tabloide.api.modules.categoria.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.categoria.interfaces.http.dto.CadastrarCategoriaRequest;
import com.tabloide.api.modules.categoria.interfaces.http.dto.EditarCategoriaRequest;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaEntity;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class CategoriaControllerIT extends CategoriaIntegrationTestSupport {

    private static final String SENHA_PADRAO = "SenhaCorreta1";

    @Autowired
    private SupermercadoJpaRepository supermercadoJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCadastrarCategoriaComoDono() throws Exception {
        Long supermercadoId = criarSupermercado("11444777003001");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-cadastra-categoria@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, "Bebidas", "Cervejas e refrigerantes"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Bebidas"))
                .andExpect(jsonPath("$.estado").value("ATIVA"));
    }

    @Test
    void deveRejeitarCadastroComOperador() throws Exception {
        Long supermercadoId = criarSupermercado("11444777003082");
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-nao-cadastra-categoria@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenOperador, "Bebidas", null))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirNomesDuplicadosNoMesmoSupermercado() throws Exception {
        Long supermercadoId = criarSupermercado("11444777003163");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-nome-duplicado-categoria@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, "Bebidas", null)).andExpect(status().isCreated());
        mockMvc.perform(cadastrar(supermercadoId, tokenDono, "Bebidas", null)).andExpect(status().isCreated());
    }

    @Test
    void deveRejeitarCadastroQuandoSupermercadoDesativado() throws Exception {
        Long supermercadoId = criarSupermercado("11444777003244");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-supermercado-desativado-categoria@sgtm.local");
        SupermercadoJpaEntity supermercado = supermercadoJpaRepository.findById(supermercadoId).orElseThrow();
        supermercado.setEstado(EstadoSupermercado.DESATIVADO);
        supermercadoJpaRepository.save(supermercado);

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, "Bebidas", null))
                .andExpect(status().isConflict());
    }

    @Test
    void deveEditarCategoriaComVersaoCorreta() throws Exception {
        Long supermercadoId = criarSupermercado("11444777003325");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-edita-categoria@sgtm.local");
        long categoriaId = cadastrarECapturarId(supermercadoId, tokenDono, "Bebidas");

        mockMvc.perform(patch("/api/supermercados/" + supermercadoId + "/categorias/" + categoriaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EditarCategoriaRequest(0L, "Bebidas Geladas", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Bebidas Geladas"));
    }

    @Test
    void deveRejeitarEdicaoComVersaoDesatualizada() throws Exception {
        Long supermercadoId = criarSupermercado("11444777003406");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-versao-desatualizada-categoria@sgtm.local");
        long categoriaId = cadastrarECapturarId(supermercadoId, tokenDono, "Bebidas");

        mockMvc.perform(patch("/api/supermercados/" + supermercadoId + "/categorias/" + categoriaId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EditarCategoriaRequest(99L, "Bebidas Geladas", null))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveDesativarEAtivarCategoria() throws Exception {
        Long supermercadoId = criarSupermercado("11444777003487");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-ativa-desativa-categoria@sgtm.local");
        long categoriaId = cadastrarECapturarId(supermercadoId, tokenDono, "Bebidas");

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/categorias/" + categoriaId + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DESATIVADA"));

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/categorias/" + categoriaId + "/ativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATIVA"));
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder cadastrar(
            Long supermercadoId, String token, String nome, String descricao) throws Exception {
        return post("/api/supermercados/" + supermercadoId + "/categorias")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CadastrarCategoriaRequest(nome, descricao)));
    }

    private long cadastrarECapturarId(Long supermercadoId, String token, String nome) throws Exception {
        String corpo = mockMvc.perform(cadastrar(supermercadoId, token, nome, null))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private Long criarSupermercado(String cnpj) {
        Instant agora = Instant.now();
        SupermercadoJpaEntity supermercado = supermercadoJpaRepository.save(new SupermercadoJpaEntity(
                null, cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP",
                null, null, null, EstadoSupermercado.ATIVO, null, agora, agora
        ));
        return supermercado.getId();
    }

    private String criarDonoEAutenticar(Long supermercadoId, String email) throws Exception {
        return criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, email);
    }

    private String criarUsuarioEAutenticar(Perfil perfil, Long supermercadoId, String email) throws Exception {
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode(SENHA_PADRAO), perfil, supermercadoId, null, true, 0, null, agora, agora
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
