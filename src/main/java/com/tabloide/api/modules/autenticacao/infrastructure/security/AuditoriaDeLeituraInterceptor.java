package com.tabloide.api.modules.autenticacao.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class AuditoriaDeLeituraInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaDeLeituraInterceptor.class);

    private final RegistradorDeAuditoriaDeLeitura registrador;

    public AuditoriaDeLeituraInterceptor(RegistradorDeAuditoriaDeLeitura registrador) {
        this.registrador = registrador;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }

        AuditarConsulta anotacao = handlerMethod.getMethodAnnotation(AuditarConsulta.class);
        if (anotacao == null || response.getStatus() < 200 || response.getStatus() >= 300) {
            return;
        }

        try {
            ContextoAutenticacao.atual().ifPresent(claims -> {
                Long entidadeId = extrairValorLong(request, anotacao.paramEntidadeId());
                Long supermercadoId = anotacao.paramSupermercadoId().isEmpty()
                        ? claims.supermercadoId()
                        : extrairValorLong(request, anotacao.paramSupermercadoId());
                registrador.registrarConsulta(
                        claims.usuarioId(), claims.perfil(), supermercadoId, anotacao.acao(), anotacao.entidade(), entidadeId);
            });
        } catch (RuntimeException e) {
            log.error("Falha ao registrar auditoria de leitura para a ação {}", anotacao.acao(), e);
        }
    }

    private Long extrairValorLong(HttpServletRequest request, String nome) {
        if (nome.isEmpty()) {
            return null;
        }

        String valor = null;
        Object variaveis = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (variaveis instanceof Map<?, ?> mapa) {
            Object bruto = mapa.get(nome);
            valor = bruto == null ? null : bruto.toString();
        }
        if (valor == null) {
            valor = request.getParameter(nome);
        }
        if (valor == null) {
            return null;
        }

        try {
            return Long.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
