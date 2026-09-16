package com.tabloide.api.modules.publicacao.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.categoria.interfaces.http.dto.CadastrarCategoriaRequest;
import com.tabloide.api.modules.loja.interfaces.http.dto.CadastrarLojaRequest;
import com.tabloide.api.modules.oferta.interfaces.http.dto.CadastrarOfertaRequest;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaRepository;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaRepository;
import com.tabloide.api.modules.produto.interfaces.http.dto.CadastrarProdutoRequest;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaEntity;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class PaginaPublicaControllerIT extends PublicacaoIntegrationTestSupport {

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
    void deveConsultarLojaPublicaSemAutenticacao() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777010043");
        String token = criarDonoEAutenticar(supermercadoId, "dono-pagina-publica@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Centro");

        mockMvc.perform(get("/api/publico/lojas/" + lojaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supermercadoId").value(supermercadoId))
                .andExpect(jsonPath("$.nomeLoja").value("Loja Centro"));
    }

    @Test
    void deveRetornar404ParaLojaInexistente() throws Exception {
        mockMvc.perform(get("/api/publico/lojas/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarSomenteOfertaVigenteERespeitarEstadoVazio() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777010124");
        String token = criarDonoEAutenticar(supermercadoId, "dono-oferta-vigente@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Centro");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, token, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, token, categoriaId);

        long ofertaVigenteId = cadastrarOfertaECapturarId(supermercadoId, token, produtoId, lojaId,
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600));
        cadastrarOfertaECapturarId(supermercadoId, token, produtoId, lojaId,
                Instant.now().plusSeconds(3600 * 24), Instant.now().plusSeconds(3600 * 48));

        mockMvc.perform(get("/api/publico/lojas/" + lojaId + "/ofertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ofertaId").value(ofertaVigenteId));
    }

    @Test
    void deveOcultarOfertaDeProdutoDesativado() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777010205");
        String token = criarDonoEAutenticar(supermercadoId, "dono-produto-desativado@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Centro");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, token, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, token, categoriaId);
        cadastrarOfertaECapturarId(supermercadoId, token, produtoId, lojaId,
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600));

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/produtos/" + produtoId + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/publico/lojas/" + lojaId + "/ofertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveFiltrarOfertasPorCategoria() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777010396");
        String token = criarDonoEAutenticar(supermercadoId, "dono-filtro-categoria@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Centro");
        long categoriaBebidas = cadastrarCategoriaECapturarId(supermercadoId, token, "Bebidas");
        long categoriaLimpeza = cadastrarCategoriaECapturarId(supermercadoId, token, "Limpeza");
        long produtoBebida = cadastrarProdutoECapturarId(supermercadoId, token, categoriaBebidas);
        long produtoLimpeza = cadastrarProdutoECapturarId(supermercadoId, token, categoriaLimpeza);
        cadastrarOfertaECapturarId(supermercadoId, token, produtoBebida, lojaId,
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600));
        cadastrarOfertaECapturarId(supermercadoId, token, produtoLimpeza, lojaId,
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600));

        mockMvc.perform(get("/api/publico/lojas/" + lojaId + "/ofertas").param("categoriaId", String.valueOf(categoriaBebidas)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoriaIds[0]").value(categoriaBebidas));
    }

    @Test
    void deveBuscarDetalhePublicoDeUmaOfertaVigente() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777010477");
        String token = criarDonoEAutenticar(supermercadoId, "dono-detalhe-oferta@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Centro");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, token, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, token, categoriaId);
        long ofertaId = cadastrarOfertaECapturarId(supermercadoId, token, produtoId, lojaId,
                Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600));

        mockMvc.perform(get("/api/publico/lojas/" + lojaId + "/ofertas/" + ofertaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ofertaId").value(ofertaId))
                .andExpect(jsonPath("$.precoPromocional").value(7.50));
    }

    @Test
    void deveRetornar404ParaOfertaNaoVigente() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777010558");
        String token = criarDonoEAutenticar(supermercadoId, "dono-oferta-nao-vigente@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, token, "Loja Centro");
        long categoriaId = cadastrarCategoriaECapturarId(supermercadoId, token, "Bebidas");
        long produtoId = cadastrarProdutoECapturarId(supermercadoId, token, categoriaId);
        long ofertaAgendadaId = cadastrarOfertaECapturarId(supermercadoId, token, produtoId, lojaId,
                Instant.now().plusSeconds(3600 * 24), Instant.now().plusSeconds(3600 * 48));

        mockMvc.perform(get("/api/publico/lojas/" + lojaId + "/ofertas/" + ofertaAgendadaId))
                .andExpect(status().isNotFound());
    }

    private long cadastrarOfertaECapturarId(
            Long supermercadoId, String token, long produtoId, long lojaId, Instant inicio, Instant fim
    ) throws Exception {
        String corpo = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/ofertas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CadastrarOfertaRequest(
                                produtoId, Set.of(lojaId), new BigDecimal("10.00"), new BigDecimal("7.50"),
                                inicio, fim, null, true))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
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

    private long cadastrarProdutoECapturarId(Long supermercadoId, String token, long categoriaId) throws Exception {
        String corpo = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/produtos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CadastrarProdutoRequest(
                                "Refrigerante Cola 2L", Set.of(categoriaId), null, null, null, null, null))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private long cadastrarLojaECapturarId(Long supermercadoId, String token, String nome) throws Exception {
        String corpo = mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/lojas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CadastrarLojaRequest(nome, "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP", null))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private Long criarSupermercadoComPlano(String cnpj) {
        Instant agora = Instant.now();
        SupermercadoJpaEntity supermercado = supermercadoJpaRepository.save(new SupermercadoJpaEntity(
                null, cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP",
                null, null, null, EstadoSupermercado.ATIVO, null, agora, agora
        ));

        PlanoJpaEntity plano = planoJpaRepository.save(new PlanoJpaEntity(
                null, "Plano " + cnpj, "plano " + cnpj, 30, BigDecimal.TEN, 100, null, null, agora, agora, null
        ));

        assinaturaJpaRepository.save(new AssinaturaJpaEntity(
                null, supermercado.getId(), plano.getId(), plano.getNome(), plano.getValidadeDias(), plano.getValor(),
                plano.getLimiteFotos(), EstadoAssinatura.VIGENTE, agora, agora.plusSeconds(3600 * 24 * 30), agora
        ));

        return supermercado.getId();
    }

    private String criarDonoEAutenticar(Long supermercadoId, String email) throws Exception {
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode(SENHA_PADRAO), Perfil.DONO, supermercadoId, null, true, 0, null, agora, agora
        ));
        String corpo = mockMvc.perform(post("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, SENHA_PADRAO))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(corpo, LoginResponse.class).token();
    }
}
