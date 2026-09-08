package com.tabloide.api.modules.produto.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.categoria.interfaces.http.dto.CadastrarCategoriaRequest;
import com.tabloide.api.modules.produto.interfaces.http.dto.CadastrarProdutoRequest;
import com.tabloide.api.modules.produto.interfaces.http.dto.AssociarCategoriasProdutoRequest;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaEntity;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaRepository;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class ProdutoControllerIT extends ProdutoIntegrationTestSupport {

    private static final String SENHA_PADRAO = "SenhaCorreta1";

    @Autowired
    private SupermercadoJpaRepository supermercadoJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCadastrarProdutoComoDono() throws Exception {
        Long supermercadoId = criarSupermercado("11444777004078");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-cadastra-produto@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestProduto(Set.of(categoriaId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Refrigerante Cola 2L"))
                .andExpect(jsonPath("$.estado").value("ATIVO"));
    }

    @Test
    void deveRejeitarCadastroComOperador() throws Exception {
        Long supermercadoId = criarSupermercado("11444777004159");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-cria-categoria-para-operador@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-nao-cadastra-produto@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenOperador, requestProduto(Set.of(categoriaId))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarQuandoCategoriaNaoExiste() throws Exception {
        Long supermercadoId = criarSupermercado("11444777004230");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-categoria-inexistente@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestProduto(Set.of(999999L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarQuandoCategoriaEstaDesativada() throws Exception {
        Long supermercadoId = criarSupermercado("11444777004310");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-categoria-desativada@sgtm.local");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/categorias/" + categoriaId + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk());

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestProduto(Set.of(categoriaId))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCadastroSemCategoria() throws Exception {
        Long supermercadoId = criarSupermercado("11444777004400");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-produto-sem-categoria@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestProduto(Set.of())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveSubstituirCategoriasDoProduto() throws Exception {
        Long supermercadoId = criarSupermercado("11444777004582");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-associa-categorias@sgtm.local");
        long categoriaInicial = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Bebidas");
        long novaCategoria = cadastrarCategoriaECapturarId(supermercadoId, tokenDono, "Ofertas");
        String produtoCriado = mockMvc.perform(cadastrar(supermercadoId, tokenDono, requestProduto(Set.of(categoriaInicial))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long produtoId = objectMapper.readTree(produtoCriado).get("id").asLong();
        long versao = objectMapper.readTree(produtoCriado).get("versao").asLong();

        mockMvc.perform(put("/api/supermercados/" + supermercadoId + "/produtos/" + produtoId + "/categorias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AssociarCategoriasProdutoRequest(versao, Set.of(novaCategoria)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoriaIds[0]").value(novaCategoria));
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder cadastrar(
            Long supermercadoId, String token, CadastrarProdutoRequest request) throws Exception {
        return post("/api/supermercados/" + supermercadoId + "/produtos")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private CadastrarProdutoRequest requestProduto(Set<Long> categoriaIds) {
        return new CadastrarProdutoRequest("Refrigerante Cola 2L", categoriaIds, "Marca X", null, null, null, null);
    }

    private long cadastrarCategoriaECapturarId(Long supermercadoId, String token, String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/categorias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CadastrarCategoriaRequest(nome, null))))
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
