package com.tabloide.api.modules.autenticacao.infrastructure.security;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

@ExtendWith(MockitoExtension.class)
class AuditoriaDeLeituraInterceptorTest {

    @Mock
    private RegistradorDeAuditoriaDeLeitura registrador;

    private AuditoriaDeLeituraInterceptor interceptor;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @BeforeEach
    void configurar() {
        interceptor = new AuditoriaDeLeituraInterceptor(registrador);
    }

    @AfterEach
    void limparContexto() {
        ContextoAutenticacao.limpar();
    }

    private static class ControladorDeTeste {

        @AuditarConsulta(acao = "ENTIDADE_CONSULTADA", entidade = "Entidade", paramEntidadeId = "id")
        public void comEntidadeId() {
        }

        @AuditarConsulta(acao = "OUTRA_CONSULTADA", entidade = "Outra", paramSupermercadoId = "supermercadoId")
        public void comSupermercadoIdExplicito() {
        }

        public void semAnotacao() {
        }
    }

    private HandlerMethod handlerCom(String nomeMetodo) throws NoSuchMethodException {
        return new HandlerMethod(new ControladorDeTeste(), ControladorDeTeste.class.getMethod(nomeMetodo));
    }

    @Test
    void naoDeveRegistrarQuandoHandlerNaoTemAnotacao() throws Exception {
        response.setStatus(200);

        interceptor.afterCompletion(request, response, handlerCom("semAnotacao"), null);

        verifyNoInteractions(registrador);
    }

    @Test
    void naoDeveRegistrarQuandoHandlerNaoForHandlerMethod() {
        interceptor.afterCompletion(request, response, new Object(), null);

        verifyNoInteractions(registrador);
    }

    @Test
    void naoDeveRegistrarQuandoRespostaNaoForSucesso() throws Exception {
        response.setStatus(403);

        interceptor.afterCompletion(request, response, handlerCom("comEntidadeId"), null);

        verifyNoInteractions(registrador);
    }

    @Test
    void naoDeveRegistrarQuandoNaoHaContextoAutenticado() throws Exception {
        response.setStatus(200);

        interceptor.afterCompletion(request, response, handlerCom("comEntidadeId"), null);

        verifyNoInteractions(registrador);
    }

    @Test
    void deveRegistrarComEntidadeIdDoPathVariableESupermercadoIdDoAtor() throws Exception {
        response.setStatus(200);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("id", "7"));
        ContextoAutenticacao.definir(new ClaimsSessao(9L, Perfil.SUPER_ADMIN, 3L, "jti"));

        interceptor.afterCompletion(request, response, handlerCom("comEntidadeId"), null);

        verify(registrador).registrarConsulta(9L, Perfil.SUPER_ADMIN, 3L, "ENTIDADE_CONSULTADA", "Entidade", 7L);
    }

    @Test
    void deveResolverSupermercadoIdViaQueryParamQuandoAnotado() throws Exception {
        response.setStatus(200);
        request.setParameter("supermercadoId", "55");
        ContextoAutenticacao.definir(new ClaimsSessao(9L, Perfil.SUPER_ADMIN, null, "jti"));

        interceptor.afterCompletion(request, response, handlerCom("comSupermercadoIdExplicito"), null);

        verify(registrador).registrarConsulta(9L, Perfil.SUPER_ADMIN, 55L, "OUTRA_CONSULTADA", "Outra", null);
    }

    @Test
    void naoDeveLancarExcecaoQuandoRegistradorFalha() throws Exception {
        response.setStatus(200);
        ContextoAutenticacao.definir(new ClaimsSessao(9L, Perfil.SUPER_ADMIN, 3L, "jti"));
        doThrow(new RuntimeException("falha simulada"))
                .when(registrador).registrarConsulta(9L, Perfil.SUPER_ADMIN, 3L, "ENTIDADE_CONSULTADA", "Entidade", null);

        interceptor.afterCompletion(request, response, handlerCom("comEntidadeId"), null);
    }
}
