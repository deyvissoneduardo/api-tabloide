package com.tabloide.api.modules.autenticacao.interfaces.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaEntity;
import com.tabloide.api.modules.autenticacao.infrastructure.persistence.UsuarioJpaRepository;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.CadastrarUsuarioAdministrativoRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginRequest;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.LoginResponse;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaEntity;
import com.tabloide.api.modules.supermercado.infrastructure.persistence.SupermercadoJpaRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioControllerIT extends AutenticacaoIntegrationTestSupport {

    private static final String EMAIL_SUPER_ADMIN = "superadmin@sgtm.local";
    private static final String SENHA_SUPER_ADMIN = "SuperAdmin@123";

    @Autowired
    private SupermercadoJpaRepository supermercadoJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveListarUsuariosDoSupermercadoComoSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001080");
        criarUsuario(Perfil.DONO, supermercadoId, "dono-lista@sgtm.local");
        criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-lista@sgtm.local");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(2));
    }

    @Test
    void deveRetornarListaVaziaParaSupermercadoSemUsuarios() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001161");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(0));
    }

    @Test
    void deveRejeitarListagemComDono() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001242");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-rejeitado@sgtm.local");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarListagemSemToken() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001323");

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveCadastrarOperadorComoDono() throws Exception {
        Long supermercadoId = criarSupermercado("11444777000080");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-cadastra-operador@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono,
                        new CadastrarUsuarioAdministrativoRequest("novo-operador@sgtm.local", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("novo-operador@sgtm.local"))
                .andExpect(jsonPath("$.perfil").value("OPERADOR"));
    }

    @Test
    void deveCadastrarDonoComoDono() throws Exception {
        Long supermercadoId = criarSupermercado("11444777000161");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-cadastra-dono@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono,
                        new CadastrarUsuarioAdministrativoRequest("novo-dono@sgtm.local", "SenhaValida1", Perfil.DONO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("DONO"));
    }

    @Test
    void deveRejeitarCadastroComEmailJaExistenteNoMesmoSupermercado() throws Exception {
        Long supermercadoId = criarSupermercado("11444777000242");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-duplicado-1@sgtm.local");
        criarUsuario(Perfil.OPERADOR, supermercadoId, "duplicado@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono,
                        new CadastrarUsuarioAdministrativoRequest("duplicado@sgtm.local", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCadastroComEmailJaExistenteEmOutroSupermercado() throws Exception {
        Long supermercadoA = criarSupermercado("11444777000323");
        Long supermercadoB = criarSupermercado("11444777000404");
        String tokenDonoA = criarUsuarioEAutenticar(Perfil.DONO, supermercadoA, "dono-duplicado-2@sgtm.local");
        criarUsuario(Perfil.OPERADOR, supermercadoB, "duplicado-global@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoA, tokenDonoA,
                        new CadastrarUsuarioAdministrativoRequest("duplicado-global@sgtm.local", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCadastroComPerfilSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercado("11444777000595");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-rejeita-super-admin@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono,
                        new CadastrarUsuarioAdministrativoRequest("outro@sgtm.local", "SenhaValida1", Perfil.SUPER_ADMIN)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarCadastroComSenhaForaDoPadrao() throws Exception {
        Long supermercadoId = criarSupermercado("11444777000676");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-rejeita-senha@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono,
                        new CadastrarUsuarioAdministrativoRequest("outro@sgtm.local", "curta", Perfil.OPERADOR)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarCadastroComEmailInvalido() throws Exception {
        Long supermercadoId = criarSupermercado("11444777000757");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-rejeita-email@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenDono,
                        new CadastrarUsuarioAdministrativoRequest("nao-e-email", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarCadastroQuandoSupermercadoBloqueado() throws Exception {
        Long supermercadoId = criarSupermercado("11444777000838", EstadoSupermercado.ATIVO);
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-bloqueado@sgtm.local");
        bloquearSupermercado(supermercadoId);

        mockMvc.perform(cadastrar(supermercadoId, tokenDono,
                        new CadastrarUsuarioAdministrativoRequest("outro@sgtm.local", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCadastroQuandoSupermercadoDesativado() throws Exception {
        Long supermercadoId = criarSupermercado("11444777000919", EstadoSupermercado.ATIVO);
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-desativado@sgtm.local");
        desativarSupermercado(supermercadoId);

        mockMvc.perform(cadastrar(supermercadoId, tokenDono,
                        new CadastrarUsuarioAdministrativoRequest("outro@sgtm.local", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarCadastroQuandoDonoForaDoEscopo() throws Exception {
        Long supermercadoA = criarSupermercado("11444777001052");
        Long supermercadoB = criarSupermercado("11444777001133");
        String tokenDonoA = criarUsuarioEAutenticar(Perfil.DONO, supermercadoA, "dono-fora-de-escopo@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoB, tokenDonoA,
                        new CadastrarUsuarioAdministrativoRequest("outro@sgtm.local", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarCadastroComOperador() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001214");
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-nao-cadastra@sgtm.local");

        mockMvc.perform(cadastrar(supermercadoId, tokenOperador,
                        new CadastrarUsuarioAdministrativoRequest("outro@sgtm.local", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarCadastroComSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001303");

        mockMvc.perform(cadastrar(supermercadoId, tokenSuperAdmin(),
                        new CadastrarUsuarioAdministrativoRequest("outro@sgtm.local", "SenhaValida1", Perfil.OPERADOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarCadastroSemToken() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001486");

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CadastrarUsuarioAdministrativoRequest("outro@sgtm.local", "SenhaValida1", Perfil.OPERADOR))))
                .andExpect(status().isUnauthorized());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder cadastrar(
            Long supermercadoId, String token, CadastrarUsuarioAdministrativoRequest request) throws Exception {
        return post("/api/supermercados/" + supermercadoId + "/usuarios")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }

    private Long criarSupermercado(String cnpj) {
        return criarSupermercado(cnpj, EstadoSupermercado.ATIVO);
    }

    private Long criarSupermercado(String cnpj, EstadoSupermercado estado) {
        Instant agora = Instant.now();
        SupermercadoJpaEntity entidade = new SupermercadoJpaEntity(
                null, cnpj, "Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888",
                "01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP",
                null, null, null, estado, null, agora, agora
        );
        return supermercadoJpaRepository.save(entidade).getId();
    }

    private void bloquearSupermercado(Long supermercadoId) {
        alterarEstadoSupermercado(supermercadoId, EstadoSupermercado.BLOQUEADO);
    }

    private void desativarSupermercado(Long supermercadoId) {
        alterarEstadoSupermercado(supermercadoId, EstadoSupermercado.DESATIVADO);
    }

    private void alterarEstadoSupermercado(Long supermercadoId, EstadoSupermercado estado) {
        SupermercadoJpaEntity entidade = supermercadoJpaRepository.findById(supermercadoId).orElseThrow();
        entidade.setEstado(estado);
        supermercadoJpaRepository.save(entidade);
    }

    private void criarUsuario(Perfil perfil, Long supermercadoId, String email) {
        Instant agora = Instant.now();
        usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode("SenhaCorreta1"), perfil, supermercadoId, null, true, 0, null, agora, agora
        ));
    }

    private String tokenSuperAdmin() throws Exception {
        return autenticarEExtrairToken(EMAIL_SUPER_ADMIN, SENHA_SUPER_ADMIN);
    }

    private String criarUsuarioEAutenticar(Perfil perfil, Long supermercadoId, String email) throws Exception {
        String senha = "SenhaCorreta1";
        criarUsuario(perfil, supermercadoId, email);
        return autenticarEExtrairToken(email, senha);
    }

    private String autenticarEExtrairToken(String email, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/api/sessoes")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, senha))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(corpo, LoginResponse.class).token();
    }
}
