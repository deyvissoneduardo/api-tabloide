package com.tabloide.api.modules.tabloide.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.loja.interfaces.http.dto.CadastrarLojaRequest;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaRepository;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaRepository;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaEntity;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaRepository;
import com.tabloide.api.modules.tabloide.domain.TipoArquivoTabloide;
import com.tabloide.api.modules.tabloide.interfaces.http.dto.DisponibilizarTabloideRequest;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class TabloideControllerIT extends TabloideIntegrationTestSupport {

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
    void deveDisponibilizarTabloideEmPdfComoVigente() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777007093");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-tabloide-pdf@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(disponibilizar(supermercadoId, tokenDono, new DisponibilizarTabloideRequest(
                        "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivos/tabloide.pdf", 1024L,
                        Set.of(lojaId), agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("VIGENTE"))
                .andExpect(jsonPath("$.tipoArquivo").value("PDF"))
                .andExpect(jsonPath("$.lojaIds.length()").value(1));
    }

    @Test
    void deveRejeitarDisponibilizacaoSemLojas() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777007174");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-tabloide-sem-loja@sgtm.local");
        Instant agora = Instant.now();

        mockMvc.perform(disponibilizar(supermercadoId, tokenDono, new DisponibilizarTabloideRequest(
                        "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivos/tabloide.pdf", 1024L,
                        Set.of(), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarPdfAcimaDe20Mb() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777007255");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-tabloide-pdf-grande@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(disponibilizar(supermercadoId, tokenDono, new DisponibilizarTabloideRequest(
                        "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivos/tabloide.pdf", 21L * 1024 * 1024,
                        Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarDisponibilizacaoComOperador() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777007336");
        String tokenOperador = criarOperadorEAutenticar(supermercadoId, "operador-tabloide@sgtm.local");
        Instant agora = Instant.now();

        mockMvc.perform(disponibilizar(supermercadoId, tokenOperador, new DisponibilizarTabloideRequest(
                        "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivos/tabloide.pdf", 1024L,
                        Set.of(1L), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarDisponibilizacaoDeOutroSupermercado() throws Exception {
        Long supermercadoA = criarSupermercadoComPlano("11444777007417");
        Long supermercadoB = criarSupermercadoComPlano("11444777007506");
        String tokenDonoB = criarDonoEAutenticar(supermercadoB, "dono-b-tabloide@sgtm.local");
        Instant agora = Instant.now();

        mockMvc.perform(disponibilizar(supermercadoA, tokenDonoB, new DisponibilizarTabloideRequest(
                        "Ofertas da semana", TipoArquivoTabloide.PDF, "https://arquivos/tabloide.pdf", 1024L,
                        Set.of(1L), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarSemConteudoQuandoNaoHaTabloideVigente() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777007689");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-tabloide-vazio@sgtm.local");

        mockMvc.perform(buscarAtual(supermercadoId, tokenDono))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarOTabloideVigenteEIgnorarExpirado() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777007760");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-tabloide-atual@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(disponibilizar(supermercadoId, tokenDono, new DisponibilizarTabloideRequest(
                        "Tabloide expirado", TipoArquivoTabloide.PDF, "https://arquivos/expirado.pdf", 1024L,
                        Set.of(lojaId), agora.minus(10, ChronoUnit.DAYS), agora.minus(5, ChronoUnit.DAYS))))
                .andExpect(status().isCreated());

        mockMvc.perform(disponibilizar(supermercadoId, tokenDono, new DisponibilizarTabloideRequest(
                        "Tabloide vigente", TipoArquivoTabloide.PDF, "https://arquivos/vigente.pdf", 1024L,
                        Set.of(lojaId), agora.minus(1, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isCreated());

        mockMvc.perform(buscarAtual(supermercadoId, tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Tabloide vigente"))
                .andExpect(jsonPath("$.estado").value("VIGENTE"));
    }

    private MockHttpServletRequestBuilder disponibilizar(Long supermercadoId, String token, DisponibilizarTabloideRequest request) throws Exception {
        return post("/api/supermercados/" + supermercadoId + "/tabloides")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private MockHttpServletRequestBuilder buscarAtual(Long supermercadoId, String token) {
        return get("/api/supermercados/" + supermercadoId + "/tabloides/atual")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
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
        return criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, email);
    }

    private String criarOperadorEAutenticar(Long supermercadoId, String email) throws Exception {
        return criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, email);
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
