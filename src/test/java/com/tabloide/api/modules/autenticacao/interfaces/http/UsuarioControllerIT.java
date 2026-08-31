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

    @Test
    void deveDesativarUsuarioComoDono() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001567");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-desativa@sgtm.local");
        Long operadorId = criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-desativado@sgtm.local").getId();

        mockMvc.perform(acao(supermercadoId, operadorId, "desativacao", tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void deveAtivarUsuarioComoDono() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001648");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-ativa@sgtm.local");
        Long operadorId = criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-inativo@sgtm.local", false).getId();

        mockMvc.perform(acao(supermercadoId, operadorId, "ativacao", tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveDesativarUsuarioComoSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercado("11444777002458");
        Long operadorId = criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-desativado-super-admin@sgtm.local").getId();

        mockMvc.perform(acao(supermercadoId, operadorId, "desativacao", tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void deveAtivarUsuarioComoSuperAdmin() throws Exception {
        Long supermercadoId = criarSupermercado("11444777002539");
        Long operadorId = criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-inativo-super-admin@sgtm.local", false).getId();

        mockMvc.perform(acao(supermercadoId, operadorId, "ativacao", tokenSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveSerIdempotenteAoDesativarDuasVezes() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001729");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-idempotente@sgtm.local");
        Long operadorId = criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-idempotente@sgtm.local").getId();

        mockMvc.perform(acao(supermercadoId, operadorId, "desativacao", tokenDono)).andExpect(status().isOk());
        mockMvc.perform(acao(supermercadoId, operadorId, "desativacao", tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void deveEncerrarSessaoDoUsuarioAoDesativar() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001800");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-encerra-sessao@sgtm.local");
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-com-sessao@sgtm.local");
        Long operadorId = usuarioJpaRepository.findByEmail("operador-com-sessao@sgtm.local").orElseThrow().getId();

        mockMvc.perform(acao(supermercadoId, operadorId, "desativacao", tokenDono)).andExpect(status().isOk());

        mockMvc.perform(get("/api/supermercados/" + supermercadoId + "/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOperador))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarDesativacaoComOperador() throws Exception {
        Long supermercadoId = criarSupermercado("11444777001961");
        String tokenOperador = criarUsuarioEAutenticar(Perfil.OPERADOR, supermercadoId, "operador-nao-desativa@sgtm.local");
        Long outroOperadorId = criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-alvo@sgtm.local").getId();

        mockMvc.perform(acao(supermercadoId, outroOperadorId, "desativacao", tokenOperador))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarDesativacaoQuandoUsuarioForaDoEscopoDoAtor() throws Exception {
        Long supermercadoA = criarSupermercado("11444777002042");
        Long supermercadoB = criarSupermercado("11444777002123");
        String tokenDonoA = criarUsuarioEAutenticar(Perfil.DONO, supermercadoA, "dono-fora-de-escopo-desativa@sgtm.local");
        Long operadorDeB = criarUsuario(Perfil.OPERADOR, supermercadoB, "operador-fora-de-escopo-desativa@sgtm.local").getId();

        mockMvc.perform(acao(supermercadoA, operadorDeB, "desativacao", tokenDonoA))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarDesativacaoQuandoUsuarioNaoExiste() throws Exception {
        Long supermercadoId = criarSupermercado("11444777002204");
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-usuario-inexistente@sgtm.local");

        mockMvc.perform(acao(supermercadoId, 999999L, "desativacao", tokenDono))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarDesativacaoQuandoSupermercadoBloqueado() throws Exception {
        Long supermercadoId = criarSupermercado("11444777002296", EstadoSupermercado.ATIVO);
        String tokenDono = criarUsuarioEAutenticar(Perfil.DONO, supermercadoId, "dono-bloqueado-desativa@sgtm.local");
        Long operadorId = criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-bloqueado@sgtm.local").getId();
        bloquearSupermercado(supermercadoId);

        mockMvc.perform(acao(supermercadoId, operadorId, "desativacao", tokenDono))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRejeitarDesativacaoSemToken() throws Exception {
        Long supermercadoId = criarSupermercado("11444777002366");
        Long operadorId = criarUsuario(Perfil.OPERADOR, supermercadoId, "operador-sem-token@sgtm.local").getId();

        mockMvc.perform(post("/api/supermercados/" + supermercadoId + "/usuarios/" + operadorId + "/desativacao"))
                .andExpect(status().isUnauthorized());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder acao(
            Long supermercadoId, Long usuarioId, String acao, String token) {
        return post("/api/supermercados/" + supermercadoId + "/usuarios/" + usuarioId + "/" + acao)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
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

    private UsuarioJpaEntity criarUsuario(Perfil perfil, Long supermercadoId, String email) {
        return criarUsuario(perfil, supermercadoId, email, true);
    }

    private UsuarioJpaEntity criarUsuario(Perfil perfil, Long supermercadoId, String email, boolean ativo) {
        Instant agora = Instant.now();
        return usuarioJpaRepository.save(new UsuarioJpaEntity(
                null, email, passwordEncoder.encode("SenhaCorreta1"), perfil, supermercadoId, null, ativo, 0, null, agora, agora
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
