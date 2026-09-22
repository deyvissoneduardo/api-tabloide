package com.tabloide.api.modules.imagem.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.imagem.domain.FormatoImagem;
import com.tabloide.api.modules.imagem.domain.TipoVinculoImagem;
import com.tabloide.api.modules.imagem.interfaces.http.dto.RegistrarImagemRequest;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaRepository;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaRepository;
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

class ImagemControllerIT extends ImagemIntegrationTestSupport {

    private static final String SENHA_PADRAO = "SenhaCorreta1";

    @Autowired
    private SupermercadoJpaRepository supermercadoJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PlanoJpaRepository planoJpaRepository;

    @Autowired
    private AssinaturaJpaRepository assinaturaJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveRegistrarImagemComoDono() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002610", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-registra-imagem@sgtm.local");

        mockMvc.perform(registrar(tokenDono, requestImagem("Banner Verão")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeBusca").value("Banner Verão"))
                .andExpect(jsonPath("$.excluidoEm").doesNotExist());
    }

    @Test
    void deveRejeitarRegistroComOperador() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002709", null);
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-nao-registra@sgtm.local");

        mockMvc.perform(registrar(tokenOperador, requestImagem("Banner")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarSemToken() throws Exception {
        mockMvc.perform(post("/api/imagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestImagem("Banner"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarQuandoCotaDoPlanoAtingida() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002881", 1);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-cota-atingida@sgtm.local");

        mockMvc.perform(registrar(tokenDono, requestImagem("Imagem Um")))
                .andExpect(status().isCreated());

        mockMvc.perform(registrar(tokenDono, requestImagem("Imagem Dois")))
                .andExpect(status().isConflict());
    }

    @Test
    void deveExcluirImagemNaoVinculadaELiberarCota() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777002962", 1);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-exclui-imagem@sgtm.local");
        long imagemId = registrarECapturarId(tokenDono, "Imagem Única");

        mockMvc.perform(delete("/api/imagens/" + imagemId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excluidoEm").isNotEmpty());

        mockMvc.perform(registrar(tokenDono, requestImagem("Imagem Depois De Excluir")))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRejeitarExclusaoDeImagemVinculada() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003004", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-imagem-vinculada@sgtm.local");
        long imagemId = registrarECapturarId(tokenDono, "Produto Vinculado", TipoVinculoImagem.PRODUTO, 42L);

        mockMvc.perform(delete("/api/imagens/" + imagemId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarExclusaoDeImagemDeOutroSupermercado() throws Exception {
        Long supermercadoA = criarSupermercadoComPlano("11444777003187", null);
        Long supermercadoB = criarSupermercadoComPlano("11444777003268", null);
        String tokenDonoA = criarDonoEAutenticar(supermercadoA, "dono-a-imagem@sgtm.local");
        String tokenDonoB = criarDonoEAutenticar(supermercadoB, "dono-b-imagem@sgtm.local");
        long imagemDeA = registrarECapturarId(tokenDonoA, "Imagem Do A");

        mockMvc.perform(delete("/api/imagens/" + imagemDeA)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDonoB))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarImagensDoProprioSupermercado() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003349", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-lista-imagens@sgtm.local");
        registrarECapturarId(tokenDono, "Banner Um");
        registrarECapturarId(tokenDono, "Banner Dois");

        mockMvc.perform(get("/api/imagens")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .param("supermercadoId", String.valueOf(supermercadoId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(2));
    }

    @Test
    void deveFiltrarImagensPorNomeEIntervaloDeData() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003772", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-filtra-imagens@sgtm.local");
        registrarECapturarId(tokenDono, "Banner Verão");
        registrarECapturarId(tokenDono, "Logomarca Nova");

        Instant antesDoUpload = Instant.now().minusSeconds(60);
        Instant depoisDoUpload = Instant.now().plusSeconds(60);

        mockMvc.perform(get("/api/imagens")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .param("supermercadoId", String.valueOf(supermercadoId))
                        .param("nomeBusca", "banner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(1))
                .andExpect(jsonPath("$.itens[0].nomeBusca").value("Banner Verão"));

        mockMvc.perform(get("/api/imagens")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .param("supermercadoId", String.valueOf(supermercadoId))
                        .param("dataInicio", antesDoUpload.toString())
                        .param("dataFim", depoisDoUpload.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(2));

        mockMvc.perform(get("/api/imagens")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .param("supermercadoId", String.valueOf(supermercadoId))
                        .param("dataInicio", depoisDoUpload.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(0));
    }

    @Test
    void deveRejeitarListagemDeOutroSupermercadoComoDono() throws Exception {
        Long supermercadoA = criarSupermercadoComPlano("11444777003420", null);
        Long supermercadoB = criarSupermercadoComPlano("11444777003500", null);
        String tokenDonoA = criarDonoEAutenticar(supermercadoA, "dono-lista-fora-escopo@sgtm.local");

        mockMvc.perform(get("/api/imagens")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDonoA)
                        .param("supermercadoId", String.valueOf(supermercadoB)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarFormatoDeTamanhoInvalidoNoRequest() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003691", null);
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-payload-invalido@sgtm.local");

        RegistrarImagemRequest invalido = new RegistrarImagemRequest(
                "", TipoVinculoImagem.BANNER, null, "s3://bucket/x.jpg", FormatoImagem.JPG, -10);

        mockMvc.perform(registrar(tokenDono, invalido))
                .andExpect(status().isUnprocessableEntity());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder registrar(
            String token, RegistrarImagemRequest request) throws Exception {
        return post("/api/imagens")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private long registrarECapturarId(String token, String nomeBusca) throws Exception {
        return registrarECapturarId(token, nomeBusca, TipoVinculoImagem.BANNER, null);
    }

    private long registrarECapturarId(String token, String nomeBusca, TipoVinculoImagem tipoVinculo, Long vinculoId) throws Exception {
        String corpo = mockMvc.perform(registrar(token, new RegistrarImagemRequest(
                        nomeBusca, tipoVinculo, vinculoId, "s3://bucket/img.jpg", FormatoImagem.JPG, 1024)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private RegistrarImagemRequest requestImagem(String nomeBusca) {
        return new RegistrarImagemRequest(nomeBusca, TipoVinculoImagem.BANNER, null, "s3://bucket/img.jpg", FormatoImagem.JPG, 1024);
    }

    private Long criarSupermercadoComPlano(String cnpj, Integer limiteFotos) {
        Instant agora = Instant.now();
        SupermercadoJpaEntity supermercado = supermercadoJpaRepository.save(new SupermercadoJpaEntity(
                null, cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP",
                null, null, null, EstadoSupermercado.ATIVO, null, agora, agora
        ));

        PlanoJpaEntity plano = planoJpaRepository.save(new PlanoJpaEntity(
                null, "Plano " + cnpj, "plano " + cnpj, 30, BigDecimal.TEN, limiteFotos, 100, null, agora, agora, null
        ));

        assinaturaJpaRepository.save(new AssinaturaJpaEntity(
                null, supermercado.getId(), plano.getId(), plano.getNome(), plano.getValidadeDias(), plano.getValor(),
                plano.getLimiteFotos(), EstadoAssinatura.VIGENTE, agora, agora.plusSeconds(3600 * 24 * 30), agora
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
