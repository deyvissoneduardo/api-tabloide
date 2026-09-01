package com.tabloide.api.modules.qrcodes.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.infrastructure.persistence.LojaJpaEntity;
import com.tabloide.api.modules.loja.infrastructure.persistence.LojaJpaRepository;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.AssinaturaJpaRepository;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaEntity;
import com.tabloide.api.modules.plano.infrastructure.persistence.PlanoJpaRepository;
import com.tabloide.api.modules.qrcodes.interfaces.http.dto.CadastrarQrCodeRequest;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class QrCodeControllerIT extends QrCodeIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";
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
    private LojaJpaRepository lojaJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveGerarQrCodeComoDono() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003100");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-gera-qrcode@sgtm.local");
        Long lojaId = criarLojaAtiva(supermercadoId, "Loja Centro");

        mockMvc.perform(gerar(supermercadoId, lojaId, tokenDono, requestQrCode("QR Entrada")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("QR Entrada"))
                .andExpect(jsonPath("$.estado").value("ATIVO"))
                .andExpect(jsonPath("$.codigoPublico").isNotEmpty());
    }

    @Test
    void deveRejeitarGeracaoSemToken() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003281");
        Long lojaId = criarLojaAtiva(supermercadoId, "Loja Centro");

        mockMvc.perform(post(rota(supermercadoId, lojaId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestQrCode("QR Entrada"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarGeracaoComOperador() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003362");
        Long lojaId = criarLojaAtiva(supermercadoId, "Loja Centro");
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-nao-gera-qrcode@sgtm.local");

        mockMvc.perform(gerar(supermercadoId, lojaId, tokenOperador, requestQrCode("QR Entrada")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarGeracaoParaLojaDeOutroSupermercado() throws Exception {
        Long supermercadoA = criarSupermercadoComPlano("11444777003443");
        Long supermercadoB = criarSupermercadoComPlano("11444777003524");
        String tokenDonoA = criarDonoEAutenticar(supermercadoA, "dono-fora-escopo-qrcode@sgtm.local");
        Long lojaDoB = criarLojaAtiva(supermercadoB, "Loja Centro");

        mockMvc.perform(gerar(supermercadoA, lojaDoB, tokenDonoA, requestQrCode("QR Entrada")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarNomeDuplicadoNoMesmoSupermercado() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003605");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-qrcode-duplicado@sgtm.local");
        Long lojaId = criarLojaAtiva(supermercadoId, "Loja Centro");

        mockMvc.perform(gerar(supermercadoId, lojaId, tokenDono, requestQrCode("QR Entrada")))
                .andExpect(status().isCreated());
        mockMvc.perform(gerar(supermercadoId, lojaId, tokenDono, requestQrCode("  qr ENTRADA  ")))
                .andExpect(status().isConflict());
    }

    @Test
    void deveAtivarEDesativarQrCode() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003786");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-ativa-desativa-qrcode@sgtm.local");
        Long lojaId = criarLojaAtiva(supermercadoId, "Loja Centro");
        long qrCodeId = gerarECapturarId(supermercadoId, lojaId, tokenDono, "QR Entrada");

        mockMvc.perform(post(rota(supermercadoId, lojaId) + "/" + qrCodeId + "/desativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DESATIVADO"));

        mockMvc.perform(post(rota(supermercadoId, lojaId) + "/" + qrCodeId + "/ativacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATIVO"));
    }

    @Test
    void deveListarQrCodesDaLojaComoDono() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003867");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-lista-qrcodes@sgtm.local");
        Long lojaId = criarLojaAtiva(supermercadoId, "Loja Centro");
        gerarECapturarId(supermercadoId, lojaId, tokenDono, "QR Um");
        gerarECapturarId(supermercadoId, lojaId, tokenDono, "QR Dois");

        mockMvc.perform(get(rota(supermercadoId, lojaId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(2));
    }

    @Test
    void deveBuscarQrCodePorIdComoSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777003948");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-busca-qrcode@sgtm.local");
        Long lojaId = criarLojaAtiva(supermercadoId, "Loja Centro");
        long qrCodeId = gerarECapturarId(supermercadoId, lojaId, tokenDono, "QR Entrada");

        mockMvc.perform(get(rota(supermercadoId, lojaId) + "/" + qrCodeId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(qrCodeId));
    }

    @Test
    void deveBaixarImagemPngESvg() throws Exception {
        Long supermercadoId = criarSupermercadoComPlano("11444777004029");
        String tokenDono = criarDonoEAutenticar(supermercadoId, "dono-baixa-qrcode@sgtm.local");
        Long lojaId = criarLojaAtiva(supermercadoId, "Loja Centro");
        long qrCodeId = gerarECapturarId(supermercadoId, lojaId, tokenDono, "QR Entrada");

        mockMvc.perform(get(rota(supermercadoId, lojaId) + "/" + qrCodeId + "/imagem")
                        .param("formato", "png")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/png"));

        mockMvc.perform(get(rota(supermercadoId, lojaId) + "/" + qrCodeId + "/imagem")
                        .param("formato", "svg")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/svg+xml"));
    }

    private String rota(Long supermercadoId, Long lojaId) {
        return "/api/supermercados/" + supermercadoId + "/lojas/" + lojaId + "/qrcodes";
    }

    private MockHttpServletRequestBuilder gerar(Long supermercadoId, Long lojaId, String token, CadastrarQrCodeRequest request) throws Exception {
        return post(rota(supermercadoId, lojaId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private long gerarECapturarId(Long supermercadoId, Long lojaId, String token, String nome) throws Exception {
        String corpo = mockMvc.perform(gerar(supermercadoId, lojaId, token, requestQrCode(nome)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("id").asLong();
    }

    private CadastrarQrCodeRequest requestQrCode(String nome) {
        return new CadastrarQrCodeRequest(nome);
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

    private Long criarLojaAtiva(Long supermercadoId, String nome) {
        Instant agora = Instant.now();
        LojaJpaEntity loja = lojaJpaRepository.save(new LojaJpaEntity(
                null, supermercadoId, nome, nome.trim().toLowerCase(), "01310-100", "Av. Paulista", "1000",
                "Bela Vista", "São Paulo", "SP", null, EstadoLoja.ATIVA, null, agora, agora
        ));
        return loja.getId();
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

    private String tokenSuperAdmin() throws Exception {
        return autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN);
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
