package com.tabloide.api.modules.conteudopromocional.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.conteudopromocional.domain.NivelAviso;
import com.tabloide.api.modules.conteudopromocional.domain.TipoConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.interfaces.http.dto.CadastrarConteudoPromocionalRequest;
import com.tabloide.api.modules.conteudopromocional.interfaces.http.dto.EditarConteudoPromocionalRequest;
import com.tabloide.api.modules.conteudopromocional.interfaces.http.dto.ReordenarConteudosPromocionaisRequest;
import com.tabloide.api.modules.loja.interfaces.http.dto.CadastrarLojaRequest;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.JsonNode;

class ConteudoPromocionalControllerIT extends ConteudoPromocionalIntegrationTestSupport {

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
    void deveCadastrarMensagemComTexto() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777008065");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-mensagem@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, new CadastrarConteudoPromocionalRequest(
                        TipoConteudoPromocional.MENSAGEM, "Feliz Natal", "Aproveite nossas ofertas", null, null,
                        Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("MENSAGEM"))
                .andExpect(jsonPath("$.posicao").value(0))
                .andExpect(jsonPath("$.estado").value("VIGENTE"));
    }

    @Test
    void deveCadastrarAvisoComNivel() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777008146");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-aviso@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, new CadastrarConteudoPromocionalRequest(
                        TipoConteudoPromocional.AVISO, "Loja fechará mais cedo", "Fechamos às 18h hoje", NivelAviso.ATENCAO, null,
                        Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nivel").value("ATENCAO"));
    }

    @Test
    void deveCadastrarBannerComDestinoHttps() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777008227");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-banner@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, new CadastrarConteudoPromocionalRequest(
                        TipoConteudoPromocional.BANNER, "Semana do consumidor", null, null, "https://exemplo.com/promo",
                        Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destino").value("https://exemplo.com/promo"));
    }

    @Test
    void deveRejeitarMensagemSemTexto() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777008308");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-mensagem-sem-texto@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();

        mockMvc.perform(cadastrar(supermercadoId, tokenDono, new CadastrarConteudoPromocionalRequest(
                        TipoConteudoPromocional.MENSAGEM, "Sem texto", null, null, null,
                        Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void deveRejeitarCadastroComOperador() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777008499");
        String tokenOperador = criarOperadorEAutenticar(supermercadoId, "operador-promocional@sgtm.local");
        Instant agora = Instant.now();

        mockMvc.perform(cadastrar(supermercadoId, tokenOperador, new CadastrarConteudoPromocionalRequest(
                        TipoConteudoPromocional.MENSAGEM, "Título", "Texto", null, null,
                        Set.of(1L), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarCadastroDeOutroSupermercado() throws Exception {
        Long supermercadoA = criarSupermercadoComPlano("11444777008570");
        Long supermercadoB = criarSupermercadoComPlano("11444777008650");
        String tokenDonoB = criarDonoEAutenticar(supermercadoB, "dono-b-promocional@sgtm.local");
        Instant agora = Instant.now();

        mockMvc.perform(cadastrar(supermercadoA, tokenDonoB, new CadastrarConteudoPromocionalRequest(
                        TipoConteudoPromocional.MENSAGEM, "Título", "Texto", null, null,
                        Set.of(1L), agora, agora.plus(1, ChronoUnit.DAYS))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveEditarConteudoAtualizandoValidade() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777008731");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-editar-promocional@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();
        JsonNode criado = cadastrarECapturarCorpo(supermercadoId, tokenDono, new CadastrarConteudoPromocionalRequest(
                TipoConteudoPromocional.MENSAGEM, "Título original", "Texto original", null, null,
                Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS)));

        mockMvc.perform(editar(supermercadoId, tokenDono, criado.get("id").asLong(), new EditarConteudoPromocionalRequest(
                        criado.get("versao").asLong(), "Título editado", "Texto editado", null, null,
                        Set.of(lojaId), agora, agora.plus(5, ChronoUnit.DAYS))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Título editado"))
                .andExpect(jsonPath("$.texto").value("Texto editado"));
    }

    @Test
    void deveRejeitarEdicaoComVersaoDesatualizada() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777008812");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-versao-promocional@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();
        JsonNode criado = cadastrarECapturarCorpo(supermercadoId, tokenDono, new CadastrarConteudoPromocionalRequest(
                TipoConteudoPromocional.MENSAGEM, "Título", "Texto", null, null,
                Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS)));

        mockMvc.perform(editar(supermercadoId, tokenDono, criado.get("id").asLong(), new EditarConteudoPromocionalRequest(
                        999L, "Título editado", "Texto editado", null, null,
                        Set.of(lojaId), agora, agora.plus(5, ChronoUnit.DAYS))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveReordenarConteudosPromocionais() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777008901");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-reordenar@sgtm.local");
        long lojaId = cadastrarLojaECapturarId(supermercadoId, tokenDono, "Loja Um");
        Instant agora = Instant.now();
        JsonNode primeiro = cadastrarECapturarCorpo(supermercadoId, tokenDono, new CadastrarConteudoPromocionalRequest(
                TipoConteudoPromocional.MENSAGEM, "Primeiro", "Texto", null, null, Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS)));
        JsonNode segundo = cadastrarECapturarCorpo(supermercadoId, tokenDono, new CadastrarConteudoPromocionalRequest(
                TipoConteudoPromocional.MENSAGEM, "Segundo", "Texto", null, null, Set.of(lojaId), agora, agora.plus(1, ChronoUnit.DAYS)));
        long idPrimeiro = primeiro.get("id").asLong();
        long idSegundo = segundo.get("id").asLong();

        mockMvc.perform(patch("/api/supermercados/" + supermercadoId + "/conteudos-promocionais/reordenacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReordenarConteudosPromocionaisRequest(List.of(idSegundo, idPrimeiro)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(idSegundo))
                .andExpect(jsonPath("$[0].posicao").value(0))
                .andExpect(jsonPath("$[1].id").value(idPrimeiro))
                .andExpect(jsonPath("$[1].posicao").value(1));
    }

    private MockHttpServletRequestBuilder cadastrar(Long supermercadoId, String token, CadastrarConteudoPromocionalRequest request) throws Exception {
        return post("/api/supermercados/" + supermercadoId + "/conteudos-promocionais")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private MockHttpServletRequestBuilder editar(Long supermercadoId, String token, Long id, EditarConteudoPromocionalRequest request) throws Exception {
        return patch("/api/supermercados/" + supermercadoId + "/conteudos-promocionais/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private JsonNode cadastrarECapturarCorpo(Long supermercadoId, String token, CadastrarConteudoPromocionalRequest request) throws Exception {
        String corpo = mockMvc.perform(cadastrar(supermercadoId, token, request))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo);
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
